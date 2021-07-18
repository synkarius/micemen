package com.explosionduck.micemen.calculation;

import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.command.grid.ProgressCommand;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.util.Util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * A tree structure meant to be able to contain the maximum number of moves for
 * up to lookahead=5. Recursively moves through nodes, setting up, evaluating, clearing.
 * <br>
 * This whole class is only used in {@link com.explosionduck.micemen.control.computer.TreeSearchComputerPlayer}.
 *
 * <pre>
 *
 * ~ Represents a tree in flat arrays ~
 *
 * So, this tree... :
 * a
 * b       b       b
 * c c c   c c c   c c c
 *
 * </pre>
 */
public class SimulationFlatTree implements ChoiceTree {

    private enum TaskType {
        /** run a ColumnShift and score the board */
        ANALYZE,
        /** get the best node from the top level */
        CHOOSE,
        /** wipe out a level */
        WIPE_LEVEL,
        /** create children level for a level */
        SETUP_CHILDREN
    }

    /** 12 players per team; 1 per column if maximally spread out; can choose up or down, so 12x2 */
    private static final int MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN = 24;
    /** each of the 24^1 choices from lookahead=1 can lead to another 24 choices the next round ... etc. */
    private static final int LV2_INC = Util.intPow(MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN, 2);
    private static final int LV3_INC = Util.intPow(MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN, 3);
    private static final int LV4_INC = Util.intPow(MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN, 4);
    private static final int LV5_INC = Util.intPow(MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN, 5);

    private static final int[] INCREMENTS = new int[]{MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN, LV2_INC, LV3_INC, LV4_INC, LV5_INC};
    private static final SimulationNode[] EMPTY_TOP = new SimulationNode[MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN];
    private final SimulationNode[][] LVLS = {                                                        //
            new SimulationNode[MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN],                                                                         //
            new SimulationNode[LV2_INC],                                                                        //
            new SimulationNode[LV3_INC],                                                                        //
            new SimulationNode[LV4_INC],                                                                        //
            new SimulationNode[LV5_INC]                                                                         //
    };

    private final ExecutorService executorService;
    private final GridCalculator calculator;
    private final int maxDepth;
    private final CheeseGrid grid;
    private final Team team;
    private final List<Future<?>> work;
    /** used for readiness indicator bar */
    private double totalWork;
    private int currentDepth;

    public SimulationFlatTree(
            ExecutorService executorService,
            GridCalculator calculator,
            CheeseGrid grid,
            Team team,
            int maxDepth) {
        this.executorService = executorService;
        this.calculator = calculator;
        this.grid = grid;
        this.team = team;
        this.maxDepth = maxDepth;
        this.work = new ArrayList<>();
    }

    /**
     * Goes through the specified level/"depth", does the given Task
     *
     * @param taskType
     * @param depth
     */
    private SimulationNode execute(TaskType taskType, int depth) {
        if (depth > LVLS.length - 1) {
            throw new CheeseException("Depth exceeds max depth.");
        }

        /* the SETUP section assumes we're on the parent level */
        if (taskType == TaskType.SETUP_CHILDREN) {
            depth -= 1;
        }

        SimulationNode[] simNodesForThisLevel = LVLS[depth];
        SimulationNode best = null;

        thisLevelLoop: for (var i = 0; i < simNodesForThisLevel.length; i++) {

            if (taskType == TaskType.WIPE_LEVEL) {
                simNodesForThisLevel[i] = null;
                continue thisLevelLoop;
            } else if (taskType == TaskType.CHOOSE) {
                if (simNodesForThisLevel[i] == null)
                    continue thisLevelLoop;

                if (best == null || simNodesForThisLevel[i].getValue() > best.getValue())
                    best = simNodesForThisLevel[i];
                continue thisLevelLoop;
            } else if (taskType == TaskType.ANALYZE) {
                if (simNodesForThisLevel[i] == null)
                    continue thisLevelLoop;
                /*
                 * TODO: this is a major limitation if the description is correct. Needs fixing.
                 * One analysis per frame; more than that and you have to worry about threads potentially;
                 */
                final int ii = i;
                Future<?> future = executorService.submit(() -> Analysis.analyzeShift(simNodesForThisLevel[ii]));
                work.add(future);

            } else if (taskType == TaskType.SETUP_CHILDREN) {
                if (simNodesForThisLevel[i] == null) {
                    continue thisLevelLoop;
                }

                // 0-depth choices will be set up in another method
                CheeseGrid copyGrid = simNodesForThisLevel[i].getCopyGrid();
                ColumnShiftCommand[] choices = this.calculator.getChoices(copyGrid);

                // all[i] is the parent
                int childStartIndex = i * MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN;
                SimulationNode[] childrenLevel = LVLS[depth + 1];

                childrenLoop: for (int k = 0; k < choices.length; k++) {
                    if (choices[k] == null) {
                        continue childrenLoop;
                    }

                    SimulationNode child = new SimulationNode(choices[k], new CheeseGrid(copyGrid), team, null);
                    childrenLevel[childStartIndex + k] = child;
                }
            }

        }

        return best;
    }

    @Override
    public boolean isReady() {
        return work.isEmpty() && currentDepth >= maxDepth;
    }

    @Override
    public int getLookAhead() {
        return this.maxDepth;
    }

    @Override
    public void clear() {
        for (int i = 0; i < LVLS.length; i++) {
            execute(TaskType.WIPE_LEVEL, i);
        }
        currentDepth = 0;
    }

    @Override
    public ColumnShiftCommand getBestFuture(int depth) {
        int[] score = new int[MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN];
        int[] count = new int[MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN];
        SimulationNode[] level = LVLS[depth];
        int increment = depth > 0 ? INCREMENTS[depth - 1] : 1;

        // sum the future values of the children
        for (int i = 0; i < level.length; i++) {
            if (level[i] == null) {
                continue;
            }
            int parentIndex = i / increment;
            score[parentIndex] += level[i].getValue();
            count[parentIndex]++;
        }

        // determine the best choice
        Integer best = null;
        for (int j = 0; j < score.length; j++) {
            if (LVLS[0][j] == null) {
                continue;
            }

            // at end of game, there may be no more
            // branches to count -- at that point, score
            // the most complex available branch
            if (score[j] == 0 && count[j] == 0) {
                return getBestFuture(depth - 1);
            }

            // get average
            score[j] /= count[j];

            if (best == null || score[best] < score[j]) {
                best = j;
            }
        }

        return LVLS[0][best].getColumnShiftCommand();
    }

    /**
     * Cycles between SETUP and ANALYZE, can only return a {@link ProgressCommand}.
     */
    @Override
    public Command process() {
        // clear finished tasks
        work.removeIf(Future::isDone);
        // wait until everything is complete
        if (!work.isEmpty()) {
            return waitCommand();
        }

        // if done analyzing, but thread slightly late, do nothing
        if (currentDepth > maxDepth) {
            return waitCommand();
        }

        // if there's no work processing, setup for analysis
        if (currentDepth == 0) {
            populateTopLevel();
        } else {
            execute(TaskType.SETUP_CHILDREN, currentDepth);
        }

        // setup multithreaded analysis
        execute(TaskType.ANALYZE, currentDepth);
        totalWork = work.size();

        // bump level
        currentDepth++;

        return waitCommand();
    }

    private Command waitCommand() {
        return new ProgressCommand(totalWork - work.size(), totalWork);
    }

    private void populateTopLevel() {
        // handle first level being empty
        if (Arrays.equals(LVLS[0], EMPTY_TOP)) {
            ColumnShiftCommand[] choices = this.calculator.getChoices(grid);
            for (int i = 0; i < MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN; i++) {
                if (choices[i] == null) {
                    LVLS[0][i] = null;
                } else {
                    LVLS[0][i] = new SimulationNode(choices[i], new CheeseGrid(grid), team, null);
                }
            }
        }
    }
}
