package simulate;

import java.util.ArrayList;
import java.util.List;

import control.ComputerPlayer;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;

public class NCSimNodeTree {
    private SimulationNode[] tops;
    private int              depth;
    private CheeseGrid       grid;
    private Team             team;
    private Team             opponent;
    
    public NCSimNodeTree(CheeseGrid grid, Team team, int depth) {
        this.tops = new SimulationNode[40];
        this.depth = depth;
        this.grid = grid;
        this.team = team;
    }
    
    private boolean topsIsEmpty() {
        return tops[0] == null;
    }
    
    /** fill in the tree */
    public void fill() {
        if (topsIsEmpty()) {
            /** assuming that this player is the active team in the grid */
            
//            List<ColumnShift> possibilities = ComputerPlayer.getChoices(grid);
//            for (int s = 0; s < possibilities.size(); s++) {
//                ColumnShift shift = possibilities.get(s);
//                CheeseGrid copygrid = new CheeseGrid(grid);
//                SimulationNode node = SimulationNode.analyzeShift(shift, copygrid, team);
//            }
            
        }
    }
    
}
