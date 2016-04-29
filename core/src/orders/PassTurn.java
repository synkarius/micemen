package orders;

import java.util.function.Consumer;

import graphical.Resource.Graphic;
import model.Block;
import model.CheeseGrid;
import model.Mouse.Team;

public class PassTurn implements IOrder {
    
    @Override
    public void execute(CheeseGrid grid) {
        grid.X(null);
        
        boolean doBlue = grid.activeTeam() == Team.RED;
        
        /** set poles and get leftmost */
        Team nextTeam = doBlue ? Team.BLUE : Team.RED;
        Integer leftmost = null;
        int count = 0;
        int only = -10;
        for (int x = 0; x < grid.width(); x++) {
            boolean hasMice = grid.ctrl().columnMouseCount(x, nextTeam) > 0;
            
            grid.poles()[x] = hasMice;
            
            if (hasMice) {
                count++;
                only = x;
            }
            
            if (x == grid.activePole()) {
                if (hasMice) // only activate the "X" when the pole is visible
                    grid.X(x);
                continue; // not eligible position for "leftmost"
            }
            
            if (leftmost == null && hasMice)
                leftmost = x;
        }
        
        // set hand on screen
        Consumer<Integer> currentHand = doBlue ? grid::redHand : grid::blueHand;
        Consumer<Integer> nextHand = doBlue ? grid::blueHand : grid::redHand;
        // deactivate current hand
        currentHand.accept(null);
        grid.activeTeam(nextTeam);
        
        // update animations
        changeTeamAnimations(grid, nextTeam);
        
        // section to handle if there's only 1 pole:
        if (count < 2) {
            grid.X(null);
            leftmost = only;
        }
        
        // activate the next hand
        grid.activePole(leftmost);
        nextHand.accept(leftmost);
        
        // finally, record
        grid.recording().pass();
        grid.recording().board(grid);
        grid.recording().team(nextTeam);
    }
    
    @Override
    public boolean finished() {
        return true;
    }
    
    @Override
    public OrderType type() {
        return OrderType.PASS_TURN;
    }
    
    private void changeTeamAnimations(CheeseGrid grid, Team active) {
        for (int x = 1; x < grid.width() - 1; x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                if (block.isMouse()) {
                    boolean shouldPoint = block.isRedMouse() && Team.RED == active;
                    shouldPoint |= block.isBlueMouse() && Team.BLUE == active;
                    
                    block.graphic(shouldPoint ? Graphic.POINT : Graphic.STAND);
                }
            }
        }
    }
    
}
