package control;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import gridparts.GridController.Scores;
import model.Block;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;

public abstract class ComputerPlayer implements IController {
    
    private static final int                    BLUE_OFFSET    = 1;
    private static final int                    MOUSE_PRESENCE = 20;
    
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
    
    public static int measureGridValue(CheeseGrid grid, Team team) {
        int score = 0;
        
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                if (block.isMouse())
                    if (team == Team.RED) {
                        score += x;
                    } else if (team == Team.BLUE) {
                        score += grid.width() - x - BLUE_OFFSET;
                    }
            }
        }
        
        Scores scores = grid.ctrl().scores();
        if (team == Team.RED) {
            score += scores.red * MOUSE_PRESENCE;
            score -= scores.blue * MOUSE_PRESENCE;
        } else if (team == Team.BLUE) {
            score += scores.blue * MOUSE_PRESENCE;
            score -= scores.red * MOUSE_PRESENCE;
        }
        
        return score;
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
    
    protected static List<ColumnShift> getChoices(CheeseGrid grid) {
        List<ColumnShift> choices = new ArrayList<>();
        
        for (int p = 0; p < grid.poles().length; p++) {
            if (grid.poles()[p] && grid.ctrl().poleIsAvailable(p)) {
                choices.add(new ColumnShift(p, Direction.UP));
                choices.add(new ColumnShift(p, Direction.DOWN));
            }
        }
        return choices;
    }
    
    public abstract ComputerPlayer copy();
    
}
