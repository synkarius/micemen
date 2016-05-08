package control;

import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.Progress;
import orders.SetHand;
import simulate.SimNodeTree;
import simulate.SimulationNode;

public class ComputerPlayerMid extends ComputerPlayerBasic {
    
    private ComputerPlayer opponent  = new ComputerPlayerBasic();
    private int            lookAhead = 2;
    private SimNodeTree    tree;
    /** true so makes 1st move */
    private boolean        hasChosen = true;
    
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
        this.tree = new SimNodeTree(grid, team, lookAhead);
    }
    
    public ComputerPlayerMid opponent(ComputerPlayer opponent) {
        this.opponent = opponent.copy();
        if (this.opponent instanceof ComputerPlayerMid)
            throw new CheeseException("Decision process for Mid vs Mid create infinite recursion.");
        return this;
    }
    
    public ComputerPlayerMid lookAhead(int lookAhead) {
        this.lookAhead = lookAhead;
        return this;
    }
    
    @Override
    public IOrder getOrder() throws CheeseException {
        if (hasChosen) {
            if (grid.lastChosen() != null)
                tree.prune(grid.lastChosen(), true);
            hasChosen = false;
        }
        if (!tree.isReady() || !hasChosen) {
            tree.doWork();
            if (!tree.isReady())
                return new Progress(tree.readyNodes(), tree.totalNodes());
        }
        
        SimulationNode best = tree.getBestWeightedFuture();
        hasChosen = true;
        tree.prune(best, false);
        
        Combo combo = new Combo();
        combo.add(new Progress(0, 0));
        combo.add(new SetHand(grid, best.x()));
        combo.add(new ColumnShift(best.x(), best.dir()));
        
        grid.activePole(best.x());
        
        return combo;
    }
    
    @Override
    public ComputerPlayer copy() {
        ComputerPlayerMid copy = new ComputerPlayerMid();
        copy.team = this.team;
        copy.lookAhead = this.lookAhead;
        return copy;
    }
    
}
