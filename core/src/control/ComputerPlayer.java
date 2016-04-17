package control;

import java.util.ArrayList;
import java.util.List;

import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.SetHand;

public class ComputerPlayer implements IController {
    
    protected CheeseGrid grid;
    protected Team       team;
    
    public ComputerPlayer grid(CheeseGrid grid) {
        this.grid = grid;
        return this;
    }
    
    public ComputerPlayer team(Team team) {
        this.team = team;
        return this;
    }
    
    @Override
    public IOrder getOrder() throws CheeseException {
        List<ColumnShift> choices = getChoices();
        int select = (int) (Math.random() * choices.size());
        ColumnShift choice = choices.get(select);
        
        Combo combo = new Combo().add(choice).add(new SetHand());
        
        return combo;
    }
    
    protected List<ColumnShift> getChoices() {
        List<ColumnShift> choices = new ArrayList<>();
        
        for (int p = 0; p < grid.poles().length; p++) {
            if (grid.poles()[p]) {
                choices.add(new ColumnShift(p, Direction.UP));
                choices.add(new ColumnShift(p, Direction.DOWN));
            }
        }
        
        return choices;
    }
    
}
