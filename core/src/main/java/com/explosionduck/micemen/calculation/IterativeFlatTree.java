package com.explosionduck.micemen.calculation;

import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.command.grid.ProgressCommand;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.util.Util;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Comparator.comparingInt;
import static java.util.Comparator.reverseOrder;
import static java.util.function.Function.identity;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toMap;

public class IterativeFlatTree implements ChoiceTree {

    /** 12 players per team; 1 per column if maximally spread out; can choose up or down, so 12x2 */
    private static final int MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN = 24;
    private static final int[] SUPPORTED_LOOKAHEAD = { 1, 2, 3, 4, 5 };
    /** each of the 24^1 choices from lookahead=1 can lead to another 24 choices the next round ... etc. */
    private static final Map<Integer, Integer> CAPACITY_MAP = Arrays.stream(SUPPORTED_LOOKAHEAD).boxed()
            .collect(toMap(identity(), i -> Util.intPow(MAX_POSSIBLE_NUMBER_OF_CHOICES_PER_TURN, i)));

    private final ExecutorService executorService;
    private final GridCalculator calculator;
    private final int lookAhead;
    private final CheeseGrid grid;
    private final Team team;
    private final Map<Integer, Collection<SimulationNode>> levels;
    private final Collection<Collection<SimulationNode>> analyzedLevels;
    private final Collection<Future<SimulationNode>> beingAnalyzed;

    public IterativeFlatTree(
            ExecutorService executorService, GridCalculator calculator, CheeseGrid grid, Team team, int lookAhead) {
        this.executorService = executorService;
        this.calculator = calculator;
        this.grid = grid;
        this.team = team;
        this.lookAhead = lookAhead;
        this.levels = CAPACITY_MAP.keySet().stream().collect(toMap(identity(), this::createWithCapacity));
        this.analyzedLevels = new ArrayList<>();
        this.beingAnalyzed = new ArrayList<>(this.getMaxLevelSize());
    }

    @Override
    public Command process() {

        // if anything is in the processing list
        if (!this.beingAnalyzed.isEmpty()) {
            // if it's all done processing, move the results into the appropriate level
            if (this.beingAnalyzed.stream().allMatch(Future::isDone)) {
                var completed = this.beingAnalyzed.stream()
                        .map(this::getCompletedSimNode)
                        .collect(Collectors.toList());
                this.getFirstEmptyLevel()
                        .ifPresent(i -> {
                            var level = this.levels.get(i);
                            level.addAll(completed);
                            this.analyzedLevels.add(level);
                        });
                // and clear out the processing list
                this.beingAnalyzed.clear();
            } else { // otherwise, wait for this level to finish processing
                return this.waitCommand();
            }
        }

        // nothing is processing; either the next level needs to be set up or it's all done
        this.getFirstEmptyLevel()
                .ifPresent(empty -> {
                    // the depth-th nodes level isn't populated: set up the lowest level which has nothing in it yet
                    var prev = empty - 1;
                    if (prev == 0) {
                        this.setupLevel(this.grid, null);
                    } else {
                        this.levels.get(prev)
                                .forEach(node -> this.setupLevel(node.getCopyGrid(), node.getRootColumnShiftCommand()));
//                                .map(SimulationNode::getCopyGrid)
//                                .forEach(this::setupLevel);
                    }
                });
        return waitCommand();
        /*
         TODO: maybe rather than trying to sort the end result with some kind of modulo thing, you make each simnode
           aware of its original parent's x value, so you can just iterate once over the highest analyzed level and
           take the winner, like in the old impl -- another benefit of this is that you can remove items from
           the futures array as soon as they're done and not wait for the whole thing
           -- you might even be able to skip a bunch of them in a future impl with farther lookahead
           -- in other words, rather than looking for the healthiest leaf, you could sample leaves further out
            and look for the healthiest branch
         */
    }

    @Override
    public boolean isReady() {
        return this.analyzedLevels.size() == this.lookAhead;
    }

    @Override
    public int getLookAhead() {
        return this.lookAhead;
    }

    @Override
    public ColumnShiftCommand getBestFuture(int depth) {
        return Arrays.stream(SUPPORTED_LOOKAHEAD).boxed().sorted(reverseOrder())
                .map(this.levels::get)
                .filter(not(Collection::isEmpty))
                .findFirst()
                .stream()
                .flatMap(Collection::stream)
                .max(comparingInt(SimulationNode::getValue))
                .map(SimulationNode::getRootColumnShiftCommand)
                .orElseThrow(() -> new CheeseException("No best future found."));
    }

    @Override
    public void clear() {
        this.analyzedLevels.forEach(Collection::clear);
        this.analyzedLevels.clear();
        this.beingAnalyzed.clear();
    }

    private void setupLevel(CheeseGrid grid, ColumnShiftCommand root) {
        Arrays.stream(this.calculator.getChoices(grid))
                .filter(Objects::nonNull)
                // TODO: serialize all params: choice, grid, and team; then reconstitute them in the analyze function
                //  -- this should save memory and cost very little extra cpu
                //  -- plus, the FileIO tool already has what you need to convert a board to/from a String
                .map(choice -> this.executorService.submit(() -> Analysis.analyzeShift(choice, grid, team,
                        root == null ? choice : root)))
                .forEach(this.beingAnalyzed::add);
    }

    private ProgressCommand waitCommand() {
        var complete = this.analyzedLevels.stream()
                .map(Collection::size)
                .map(Integer::doubleValue)
                .reduce(0d, Double::sum);
        var total = CAPACITY_MAP.get(this.lookAhead);
        return new ProgressCommand(complete, total.doubleValue());
    }

    private Collection<SimulationNode> createWithCapacity(int depth) {
        return depth <= this.lookAhead
                ? new ArrayList<>(CAPACITY_MAP.get(depth))
                : emptySet();
    }

    private Optional<Integer> getFirstEmptyLevel() {
        return Arrays.stream(SUPPORTED_LOOKAHEAD).boxed()
                .filter(i -> i <= this.lookAhead && this.levels.get(i).isEmpty())
                .findFirst();
    }

    private SimulationNode getCompletedSimNode(Future<SimulationNode> simulationNodeFuture) {
        try {
            return simulationNodeFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new CheeseException(e);
        }
    }

    private int getMaxLevelSize() {
        return this.levels.values().stream().map(Collection::size).reduce(Integer::max).orElse(0);
    }
}
