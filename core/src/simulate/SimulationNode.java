package simulate;

import java.util.ArrayList;
import java.util.List;

import control.Direction;
import gridparts.GridController;
import gridparts.GridController.Scores;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;

public class SimulationNode {
    
    private ColumnShift          shift;
    private CheeseGrid           grid;
    /** does not alternate, only used for scoring */
    private Team                 team;
    public boolean               analyzed;
    public SimulationNode        parent;
    private List<SimulationNode> children;
    private int                  depth;
    
    private int                  x;
    private Direction            dir;
    private int                  value;
    
    /** constructor used by ComputerPlayerBasic */
    public SimulationNode(int x, Direction dir, int value) {
        this.x = x;
        this.dir = dir;
        this.value = value;
    }
    
    /** constructor used by ComputerPlayerMid */
    public SimulationNode(ColumnShift shift, CheeseGrid grid, Team team, int depth) {
        this.shift = shift;
        this.grid = grid;
        this.team = team;
        this.depth = depth;
    }
    
    public int x() {
        return x;
    }
    
    public Direction dir() {
        return dir;
    }
    
    public int value() {
        return value;
    }
    
    /** NEW FOR ComputerPlayerMid: */
    
    public CheeseGrid grid() {
        return grid;
    }
    
    public static SimulationNode analyzeShift(ColumnShift shift, CheeseGrid copygrid, Team team) {
        // java.lang.System.out.println("- processing GID: " + copygrid.id());
        SimulationNode result = analyzeShift(shift, copygrid, team, 0);
        // java.lang.System.out.println("- finished GID: " + copygrid.id());
        return result;
    }
    
    protected static SimulationNode analyzeShift(ColumnShift shift, CheeseGrid copygrid, Team team, int depth)
            throws CheeseException {
        SimulationNode node = new SimulationNode(shift, copygrid, team, depth);
        analyzeShift(node);
        // clean:
        node.grid = null;
        node.shift = null;
        node.team = null;
        return node;
    }
    
    protected static void analyzeShift(SimulationNode node) throws CheeseException {
        
        node.grid.ctrl().orders().add(node.shift);
        node.grid.ctrl().executeAll();
        
        node.x = node.shift.x();
        node.dir = node.shift.dir();
        
        Scores eval = GridController.scores(node.grid, true);
        
        node.value = node.team == Team.RED ? eval.redBoardValue : eval.blueBoardValue;
        node.analyzed = true;
    }
    
    protected List<SimulationNode> children() {
        if (children == null)
            children = new ArrayList<>();
        return children;
    }
    
    protected static void setupChildren(SimulationNode node) {
        if (node.depth > 0) {
            if (node.children().size() == 0) {
                SimNodeTree.fillLevel(node.children(), node.grid, node.team, node.depth - 1);
                SimNodeTree.linkToParent(node, node.children());
            }
        }
    }
    
    /** result indicates that something got analyzed */
    protected static boolean analyzeNextUnanalyzed(SimulationNode node) {
        if (!node.analyzed) {
            // if the node hasn't been analyzed, analyze it and generate
            // children if necessary
            analyzeShift(node);
            setupChildren(node);
            return true;
        } else if (!node.children().isEmpty()) {
            // if the node has been analyzed, check its children
            for (SimulationNode child : node.children()) {
                boolean analyzed = analyzeNextUnanalyzed(child);
                if (analyzed)
                    return true;
            }
        }
        return false;
    }
    
    protected void reset(int depth) {
        this.depth = depth;
        if (children != null)
            for (SimulationNode child : children())
                child.reset(depth - 1);
    }
    
    public boolean isReady() {
        if (!this.analyzed)
            return false;
        if (children != null)
            for (SimulationNode child : children)
                if (!child.isReady())
                    return false;
        return true;
    }
    
    /**
     * value gets boosted by depth -- so present moves get s slight boost to
     * future moves
     */
    public int weightedFuture() {
        if (children().isEmpty()) {
            return value + depth;
        } else {
            int value = 0;
            double count = 0;
            for (SimulationNode child : children()) {
                value += child.weightedFuture();
                count++;
            }
            return (int) (value / count);
        }
    }
}
