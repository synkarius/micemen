package control;

import java.util.concurrent.ExecutorService;

import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.Progress;
import orders.SetHand;
import simulate.SimFlatTree;
import simulate.SimNodeTree;
import simulate.SimulationNode;

public class ComputerPlayerMid2 extends ComputerPlayerBasic {
    
    private int         lookAhead = 2;
    private SimFlatTree tree;
    // /** true so makes 1st move */
    // private boolean hasChosen = true;
    
    public ComputerPlayerMid2(ExecutorService pool) {
        super(pool);
    }
    
    @Override
    public ComputerPlayer grid(CheeseGrid grid) {
        super.grid(grid);
        if (team != null)
            makeTree();
        return this;
    }
    
    @Override
    public ComputerPlayer team(Team team) {
        super.team(team);
        if (grid != null)
            makeTree();
        return this;
    }
    
    private void makeTree() {
        this.tree = new SimFlatTree(pool, grid, team, lookAhead);
    }
    
    public ComputerPlayerMid2 lookAhead(int lookAhead) {
        this.lookAhead = lookAhead;
        return this;
    }
    
    public int lookAhead() {
        return lookAhead;
    }
    
    @Override
    public IOrder getOrder() throws CheeseException {
        // if (hasChosen) {
        // // prune for other team's last choice:
        // if (grid.lastChosen() != null)
        // tree.prune(grid.lastChosen(), true);
        // hasChosen = false;
        // }
        
        if (!tree.isReady())
            return tree.process();
        
        SimulationNode best = tree.getBestFuture();
        // hasChosen = true;
        // tree.prune(best, false);
        tree.clear();
        
        Combo combo = new Combo();
        combo.add(new Progress(0, 0));
        combo.add(new SetHand(grid, best.x()));
        combo.add(new ColumnShift(best.x(), best.dir()));
        
        grid.activePole(best.x());
        
        return combo;
    }
    
    @Override
    public ComputerPlayer copy() {
        ComputerPlayerMid2 copy = new ComputerPlayerMid2(pool);
        copy.team = this.team;
        copy.lookAhead = this.lookAhead;
        return copy;
    }
    
}
