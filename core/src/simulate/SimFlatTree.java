package simulate;

import control.ComputerPlayer;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;

public class SimFlatTree {
    /**
     * <pre>
     * 
     * ~ Represents a tree in a flat array ~
     * 
     * So, this tree... :
     * a
     * b       b       b
     * c c c   c c c   c c c
     * 
     * ... gets represented as:
     * abbbccccccccc
     * 
     * </pre>
     */
    
    private static final int       MAX_CH       = 24;
    private static final int       LV2_INC      = (int) Math.pow(MAX_CH, 2);
    private static final int       LV3_INC      = (int) Math.pow(MAX_CH, 3);
    private static final int       LV4_INC      = (int) Math.pow(MAX_CH, 4);
    private static final int       LV5_INC      = (int) Math.pow(MAX_CH, 5);
    
    private static final int       LV1          = MAX_CH;                               // 24
    private static final int       LV2          = LV1 + LV2_INC;                        // 24+576
    private static final int       LV3          = LV2 + LV3_INC;                        // 24+576+13824
    private static final int       LV4          = LV3 + LV4_INC;                        // ...+331776
    private static final int       LV5          = LV4 + LV5_INC;                        // ...+7962624
    private static final int[]     STARTS       = new int[] { 0, LV1, LV2, LV3, LV4 };
    private static final int[]     ENDS         = new int[] { LV1, LV2, LV3, LV4, LV5 };
    
    private static final int       ANALYSIS_MAX = 2;
    
    private final SimulationNode[] all          = new SimulationNode[9000000];
    private final int              maxDepth;
    private final CheeseGrid       grid;
    private final Team             team;
    private final Team             opponent;
    
    enum Task {
        /** run a ColumnShift and score the board */
        ANALYZE,
        /** get the best node from the top level */
        CHOOSE,
        /** wipe out a level */
        WIPE,
        /** create children level for a level */
        FILL,
        /** replace a parent level with a section of a child level */
        PROMOTE
    }
    
    public SimFlatTree(CheeseGrid grid, Team team, int maxDepth) {
        this.maxDepth = maxDepth;
        this.grid = grid;
        this.team = team;
        this.opponent = team == Team.RED ? Team.BLUE : Team.RED;
    }
    
    /**
     * Goes through the appropriate level, does the given Task
     * 
     * @param task
     * @param depth
     */
    public SimulationNode execute(Task task, int depth, Integer chosen) {
        if (depth > 4)
            throw new CheeseException("Bad depth.");
        if (depth == 4 && task == Task.FILL)
            throw new CheeseException("Bad depth (Task.FILL) -- end level doesn't get children");
        
        int start = STARTS[depth];
        int end = ENDS[depth];
        int childStart = STARTS[depth + 1];
        SimulationNode best = null;
        
        loop: for (int i = start; i < end; i++) {
            
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
                if (all[i].analyzed)
                    continue loop;
                SimulationNode.analyzeShift(all[i]);
                return all[i];
            } else if (task == Task.FILL) {
                if (all[i] == null && depth != 0)
                    continue;
                // 0-depth choices will be set up in another method
                CheeseGrid grid = all[i].grid();
                ColumnShift[] choices = ComputerPlayer.getChoices(grid);
                
                // nthParent is the number of iterations thus far
                int nthParent = i - STARTS[depth];
                children: for (int k = 0; k < choices.length; k++) {
                    if (choices[k] == null)
                        continue children;
                    
                    SimulationNode child = new SimulationNode(choices[k], new CheeseGrid(grid), team, depth);
                    all[childStart + (nthParent * MAX_CH) + k] = child;
                }
            } else if (task == Task.PROMOTE) {
                // nthChild is the number of iterations thus far
                int nthChild = i - STARTS[depth];
                all[i] = all[childStart + (chosen.intValue() * MAX_CH) + nthChild];
            }
            
        }
        
        return best;
    }
    
}
