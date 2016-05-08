package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import gridparts.GridController.Scores;
import model.Block;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;

public abstract class ComputerPlayer implements IController {
    
    public static final Comparator<ColumnShift> RED_SORT;
    public static final Comparator<ColumnShift> BLUE_SORT;
    static {
        RED_SORT = (a, b) -> {
            int xCompare = Integer.compare(a.x(), b.x());
            if (xCompare != 0)
                return xCompare;
            return a.dir().compareTo(b.dir());
        };
        BLUE_SORT = (a, b) -> {
            int xCompare = Integer.compare(b.x(), a.x());
            if (xCompare != 0)
                return xCompare;
            return a.dir().compareTo(b.dir());
        };
    }
    
    
    
    protected CheeseGrid grid;
    protected Team       team;
    protected boolean    isCPUvsCPUOpponent;
    
    public ComputerPlayer grid(CheeseGrid grid) {
        this.grid = grid;
        return this;
    }
    
    public ComputerPlayer team(Team team) {
        this.team = team;
        return this;
    }
    
    public static List<ColumnShift> getChoices(CheeseGrid grid) {
        List<ColumnShift> choices = new ArrayList<>();
        
        for (int p = 0; p < grid.poles().length; p++) {
            if (grid.poles()[p] && grid.ctrl().poleIsAvailable(p)) {
                choices.add(new ColumnShift(p, Direction.UP));
                choices.add(new ColumnShift(p, Direction.DOWN));
            }
        }
        
        Comparator<ColumnShift> comparator = grid.activeTeam() == Team.RED ? RED_SORT : BLUE_SORT;
        Collections.sort(choices, comparator);
        
        return choices;
    }
    
    protected int gap() {
        int min = grid.wMax();
        int max = 0;
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                boolean countMouse = (block.isBlueMouse() && team == Team.BLUE)
                        || (block.isRedMouse() && team == Team.RED);
                if (countMouse) {
                    if (x > max)
                        max = x;
                    if (x < min)
                        x = min;
                }
            }
        }
        return Math.abs(max - min);
    }
    
    public abstract ComputerPlayer copy();
    
}
