package simulate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import control.ComputerPlayer;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.IOrder;
import orders.Progress;
import util.Util;

public class SimFlatTree {
    /**
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
    
    private static final int              MAX_CH     = 24;
    private static final int              LV2_INC    = Util.intPow(MAX_CH, 2);
    private static final int              LV3_INC    = Util.intPow(MAX_CH, 3);
    private static final int              LV4_INC    = Util.intPow(MAX_CH, 4);
    private static final int              LV5_INC    = Util.intPow(MAX_CH, 5);
    
    private static final int[]            INCREMENTS = new int[] { MAX_CH, LV2_INC, LV3_INC, LV4_INC, LV5_INC };
    private static final SimulationNode[] EMPTY_TOP  = new SimulationNode[MAX_CH];
    private final SimulationNode[][]      LVLS       = {                                                        //
            new SimulationNode[MAX_CH],                                                                         //
            new SimulationNode[LV2_INC],                                                                        //
            new SimulationNode[LV3_INC],                                                                        //
            new SimulationNode[LV4_INC],                                                                        //
            new SimulationNode[LV5_INC]                                                                         //
    };
    
    private final ExecutorService         pool;
    private final int                     maxDepth;
    private final CheeseGrid              grid;
    private final Team                    team;
    private final Team                    opponent;
    
    // used for readiness indicator bar
    private double                        total;
    private int                           currentDepth;
    private final List<Future<?>>         work;
    private Task                          nextTask   = Task.SETUP;
    
    enum Task {
        /** run a ColumnShift and score the board */
        ANALYZE,
        /** get the best node from the top level */
        CHOOSE,
        /** wipe out a level */
        WIPE,
        /** create children level for a level */
        SETUP// ,
        // /** replace a parent level with a section of a child level */
        // PROMOTE
    }
    
    public SimFlatTree(ExecutorService pool, CheeseGrid grid, Team team, int maxDepth) {
        this.maxDepth = maxDepth;
        this.grid = grid;
        this.team = team;
        this.opponent = team == Team.RED ? Team.BLUE : Team.RED;
        this.pool = pool;
        this.work = new ArrayList<>();
    }
    
    /**
     * Goes through the appropriate level, does the given Task
     * 
     * @param task
     * @param depth
     */
    private SimulationNode execute(Task task, int depth, int chosen) {
        if (depth > LVLS.length - 1)
            throw new CheeseException("Bad depth.");
   
        
        /** the SETUP section assumes we're on the parent level */
        if (task == Task.SETUP)
            depth -= 1;
        
        SimulationNode[] all = LVLS[depth];
        // int increment = INCREMENTS[depth];
        SimulationNode best = null;
        
        int i = 0;
        int max = all.length;
        // // 'p' used for promote task -- avoids needing modulo
        // int p = 0;
        // if (task == Task.PROMOTE) {
        // i = chosen * increment;
        // max = i + increment;
        // }
        
        loop: for (; i < max; i++) {
            
            if (task == Task.WIPE) {
                all[i] = null;
                continue loop;
            } else if (task == Task.CHOOSE) {
                if (all[i] == null)
                    continue loop;
                
                if (best == null || all[i].value() > best.value())
                    best = all[i];
                continue loop;
            } else if (task == Task.ANALYZE) {
                if (all[i] == null)
                    continue loop;
                // one analysis per frame
                // -- more than that and you have to worry about
                // threads potentially
                final int ii = i;
                Future<?> future = pool.submit(() -> SimulationNode.analyzeShift(all[ii]));
                work.add(future);
                
                // if (all[i].analyzed)
                // continue loop;
                // SimulationNode.analyzeShift(all[i]);
                // return all[i];
            } else if (task == Task.SETUP) {
                if (all[i] == null)
                    continue;
                
                // 0-depth choices will be set up in another method
                CheeseGrid grid = all[i].grid();
                ColumnShift[] choices = ComputerPlayer.getChoices(grid);
                
                // all[i] is the parent
                int childStartIndex = i * MAX_CH;
                SimulationNode[] childrenLevel = LVLS[depth + 1];
                
                children: for (int k = 0; k < choices.length; k++) {
                    if (choices[k] == null)
                        continue children;
                    
                    SimulationNode child = new SimulationNode(choices[k], new CheeseGrid(grid), team, depth);
                    childrenLevel[childStartIndex + k] = child;
                }
            }
            // else if (task == Task.PROMOTE) {
            // // will only promote chosen children
            // // due to altered loop indices
            // // -- we DO want to promote nulls
            // int parentIndex = p++;
            // SimulationNode[] parentLevel = LVLS[depth - 1];
            //
            // parentLevel[parentIndex] = all[i];
            // }
            
        }
        
        return best;
    }
    
    public boolean isReady() {
        return work.isEmpty() && currentDepth >= maxDepth;
    }
    
    public void clear() {
        for (int i = 0; i < LVLS.length; i++)
            execute(Task.WIPE, i);
        nextTask = Task.SETUP;
        currentDepth = 0;
    }
    
    public SimulationNode getBestFuture() {
        int[] score = new int[MAX_CH];
        int[] count = new int[MAX_CH];
        SimulationNode[] level = LVLS[maxDepth];
        int increment = INCREMENTS[maxDepth - 1];
        
        // sum the future values of the children
        for (int i = 0; i < level.length; i++) {
            if (level[i] == null)
                continue;
            int parentIndex = i / increment;
            score[parentIndex] += level[i].value();
            count[parentIndex]++;
        }
        
        // determine the best choice
        Integer best = null;
        for (int j = 0; j < score.length; j++) {
            if (LVLS[0][j] == null)
                continue;
            
            // get average
            score[j] /= count[j];
            
            if (best == null || score[best] < score[j]) 
                best = j;
            
        }
        
        return LVLS[0][best];
    }
    
    /** cycles between SETUP and ANALYZE */
    public IOrder process() {
        // clear finished tasks
        work.removeIf(future -> future.isDone());
        // wait until everything is complete
        if (!work.isEmpty())
            return waiT();
        
        // if done analyzing, but thread slightly late, do nothing
        if (currentDepth > maxDepth)
            return waiT();
        
        // if there's no work processing, setup for analysis
        if (currentDepth == 0)
            fillTop();
        else
            execute(Task.SETUP, currentDepth);
        
        // setup multithreaded analysis
        execute(Task.ANALYZE, currentDepth);
        total = work.size();
        
        // bump level
        currentDepth++;
        
        return waiT();
    }
    
    private IOrder waiT() {
        return new Progress(total - work.size(), total);
    }
    
    private void fillTop() {
        // handle first level being empty
        if (Arrays.equals(LVLS[0], EMPTY_TOP)) {
            ColumnShift choices[] = ComputerPlayer.getChoices(grid);
            for (int i = 0; i < MAX_CH; i++) {
                if (choices[i] == null)
                    LVLS[0][i] = null;
                else
                    LVLS[0][i] = new SimulationNode(choices[i], new CheeseGrid(grid), team, 0);
            }
        }
    }
    
    /**
     * method overload so don't always have to pass in a 0 when not doing a
     * promote task
     */
    public SimulationNode execute(Task task, int depth) {
        return execute(task, depth, 0);
    }
    
    /**
     * only useful if the tree has a memory -- fuck this, the gains are marginal
     * compared to the complications added
     */
    public void prune(SimulationNode choice, boolean reset) {
        //
        // int chosen = -1;
        // for (int i = 0; i < MAX_CH; i++) {
        // if (LVLS[0][i] == null)
        // continue;
        // if (LVLS[0][i].x() == choice.x() && LVLS[0][i].dir() == choice.dir())
        // {
        // chosen = i;
        // break;
        // }
        // }
        //
        // if (chosen == -1)
        // throw new CheeseException("Failed to find chosen node.");
        //
        // execute(Task.PROMOTE, 0, chosen);
    }
}
