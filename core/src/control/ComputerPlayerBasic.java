package control;

import model.CheeseException;
import model.CheeseGrid;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.SetHand;
import simulate.SimulationNode;

public class ComputerPlayerBasic extends ComputerPlayer implements IController {
    
    @Override
    public IOrder getOrder() throws CheeseException {
        
        SimulationNode best = null;
        for (ColumnShift choice : getChoices(grid)) {
            SimulationNode result = SimulationNode.analyzeShift(choice, new CheeseGrid(grid), team);
            if (best == null || result.value() > best.value())
                best = result;
        }
        
        if (best == null) {
            if (this.isCPUvsCPUOpponent)
                return null; // indicates that someone has won
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
        ComputerPlayerBasic copy = new ComputerPlayerBasic();
        copy.team = this.team;
        return copy;
    }
}
