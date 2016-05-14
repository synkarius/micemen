package control;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import model.CheeseException;
import model.CheeseGrid;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.SetHand;
import simulate.SimulationNode;

public class ComputerPlayerBasic extends ComputerPlayer implements IController {
    
    public ComputerPlayerBasic(ExecutorService pool) {
        super(pool);
    }
    
    @Override
    public IOrder getOrder() throws CheeseException {
        
        SimulationNode best = null;
        List<SimulationNode> results = ComputerPlayer.splitAndBlock(pool, getChoices(grid), grid, team);
        for (SimulationNode result : results) {
            if (best == null || result.value() > best.value())
                best = result;
        }
        
        // for (ColumnShift choice : getChoices(grid)) {
        // if (choice == null)
        // continue;
        //
        // SimulationNode result = SimulationNode.analyzeShift(choice, new
        // CheeseGrid(grid), team);
        // if (best == null || result.value() > best.value())
        // best = result;
        // }
        
        if (best == null) {
            throw new CheeseException("No choices available.", grid);
        }
        
        Combo combo = new Combo();
        combo.add(new SetHand(grid, best.x()));
        combo.add(new ColumnShift(best.x(), best.dir()));
        
        grid.activePole(best.x());
        
        return combo;
    }
    
    @Override
    public ComputerPlayer copy() {
        ComputerPlayerBasic copy = new ComputerPlayerBasic(pool);
        copy.team = this.team;
        return copy;
    }
}
