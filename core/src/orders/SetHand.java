package orders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

import control.Direction;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;

public class SetHand implements IOrder {
    
    private Team              team;
    private int               x;
    private boolean           isRelative;
    private Consumer<Integer> handSet;
    private Supplier<Integer> handGet;
    
    public SetHand(CheeseGrid grid, int x) {
        this.team = grid.activeTeam();
        this.x = x;
        this.handSet = this.team == Team.RED ? grid::redHand : grid::blueHand;
        this.handGet = this.team == Team.RED ? grid::redHand : grid::blueHand;
    }
    
    public SetHand(CheeseGrid grid, int x, boolean isRelative) {
        this(grid, x);
        this.isRelative = isRelative;
    }
    
    @Override
    public void execute(CheeseGrid grid) throws CheeseException {
        if (!isRelative) {
            handSet.accept(x);
            return;
        }
        
        Direction dir = x < 0 ? Direction.LEFT : Direction.RIGHT;
        
        List<Integer> gridIndices = new ArrayList<>();
        for (int i = 2; i < grid.width() - 2; i++) // first and last two columns
                                                   // should never be available
            gridIndices.add(i);
        if (dir == Direction.LEFT)
            Collections.reverse(gridIndices);
        
        // get available next pole in relativeX direction
        int available = 0;
        for (Integer x : gridIndices) {
            if (x == grid.X())
                continue;
            
            if (grid.ctrl().poleIsAvailable(x)) {
                boolean right = dir == Direction.RIGHT && x > handGet.get();
                boolean left = dir == Direction.LEFT && x < handGet.get();
                if (right || left) {
                    available = x;
                    break;
                }
            }
        }
        if (available != 0) {
            grid.activePole(available);
            handSet.accept(available);
        }
    }
    
    @Override
    public boolean finished() {
        return true;
    }
    
    @Override
    public OrderType type() {
        return OrderType.SET_HAND;
    }
    
}
