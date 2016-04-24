package control;

import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;

public class ValueCalc {
    
    private int       x;
    private Direction dir;
    private int       value;
    
    public ValueCalc(int x, Direction dir, int value) {
        this.x = x;
        this.dir = dir;
        this.value = value;
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
    
    public static ValueCalc analyzeShift(ColumnShift shift, CheeseGrid grid, Team team) throws CheeseException {
        
        // grid.nullifyParentLinks();
        grid.ctrl().orders().add(shift);
        grid.ctrl().executeAll();
        int postValue = ComputerPlayerBasic.measureGridValue(grid, team);
        
        return new ValueCalc(shift.x(), shift.dir(), postValue);
    }
}