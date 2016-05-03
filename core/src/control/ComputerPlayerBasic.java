package control;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.SetHand;

public class ComputerPlayerBasic extends ComputerPlayer implements IController {
    
    @Override
    public IOrder getOrder() throws CheeseException {
        
        List<ColumnShift> choices = getChoices(grid);
        Comparator<ColumnShift> comparator = team == Team.RED ? RED_SORT : BLUE_SORT;
        Collections.sort(choices, comparator);
        
        SimulationNode best = null;
        for (ColumnShift choice : choices) {
            SimulationNode result = SimulationNode.analyzeShift(choice, new CheeseGrid(grid), team);
            if (best == null || result.value() > best.value())
                best = result;
        }
        
        if (best == null)
            throw new CheeseException("No choices available.");
        
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
