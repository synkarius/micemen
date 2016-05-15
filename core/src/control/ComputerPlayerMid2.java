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
import simulate.SimulationNode;

public class ComputerPlayerMid2 extends ComputerPlayerBasic {
    
    private final int   lookAhead;
    private SimFlatTree tree;
    
    public ComputerPlayerMid2(ExecutorService pool, CheeseGrid grid, Team team, int lookAhead) {
        super(pool);
        super.grid(grid);
        super.team(team);
        this.lookAhead = lookAhead;
        this.tree = new SimFlatTree(pool, grid, team, lookAhead);
    }
    
    public int lookAhead() {
        return lookAhead;
    }
    
    @Override
    public IOrder getOrder() throws CheeseException {
        if (!tree.isReady())
            return tree.process();
        
        SimulationNode best = tree.getBestFuture(lookAhead);
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
        return new ComputerPlayerMid2(pool, grid, team, lookAhead);
    }
    
}
