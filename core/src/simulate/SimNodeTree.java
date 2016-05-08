package simulate;

import java.util.ArrayList;
import java.util.List;

import control.ComputerPlayer;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;

public class SimNodeTree {
    private List<SimulationNode> tops;
    private int                  depth;
    private CheeseGrid           grid;
    private Team                 team;
    
    public SimNodeTree(CheeseGrid grid, Team team, int depth) {
        this.tops = new ArrayList<>();
        this.depth = depth;
        this.grid = grid;
        this.team = team;
    }
    
    /**
     * creates an unanalyzed SimulationNode with a copy of the grid passed in
     */
    protected static void fillLevel(List<SimulationNode> level, CheeseGrid grid, Team team, int depth) {
        for (ColumnShift shift : ComputerPlayer.getChoices(grid))
            level.add(new SimulationNode(shift, new CheeseGrid(grid), team, depth));
    }
    
    protected static void linkToParent(SimulationNode parent, List<SimulationNode> children) {
        for (SimulationNode child : children)
            child.parent = parent;
    }
    
    public void doWork() {
        if (tops.isEmpty())
            fillLevel(tops, grid, team, depth);
        for (SimulationNode node : tops) {
            boolean analyzed = SimulationNode.analyzeNextUnanalyzed(node);
            if (analyzed)
                return;
        }
    }
    
    public SimulationNode getBestWeightedFuture() {
        int best = -9999;
        SimulationNode bestNode = null;
        for (SimulationNode node : tops) {
            int future = node.weightedFuture();
            if (future > best) {
                best = future;
                bestNode = node;
            }
        }
        return bestNode;
    }
    
    public void prune(SimulationNode choice, boolean reset) {
        if (tops.isEmpty())
            return;
        SimulationNode chosen = null;
        for (SimulationNode node : tops) {
            if (node.x() == choice.x() && node.dir() == choice.dir()) {
                chosen = node;
                break;
            }
        }
        this.tops = chosen.children();
        if (reset) {
            for (SimulationNode node : this.tops) {
                node.reset(depth);
                SimulationNode.setupChildren(node);
            }
        }
    }
    
    public boolean isReady() {
        for (SimulationNode node : tops)
            if (!node.isReady())
                return false;
        return true;
    }
    
    public int readyNodes() {
        int count = 0;
        for (SimulationNode node : tops)
            if (node.isReady())
                count++;
        return count;
    }
    
    public double totalNodes() {
        return (double) tops.size();
    }
    
}
