package control;

import java.util.List;

import gridparts.GridController.Scores;
import model.Block;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.Combo;
import orders.IOrder;
import orders.SetHand;

public class ComputerPlayerBasic extends ComputerPlayer implements IController {
    
    private static final int BLUE_OFFSET    = 1;
    private static final int MOUSE_PRESENCE = 20;
    
    @Override
    public IOrder getOrder() throws CheeseException {
        
        List<ColumnShift> choices = super.getChoices();
        ValueCalc best = null;
        for (ColumnShift choice : choices) {
            ValueCalc calc = ValueCalc.analyzeShift(choice, new CheeseGrid(grid), team);
            if (best == null || calc.value() > best.value())
                best = calc;
        }
        
        if (best == null)
            throw new CheeseException("No choices available.");
        
        Combo combo = new Combo();
        combo.add(new SetHand(grid, best.x()));
        combo.add(new ColumnShift(best.x(), best.dir()));
        
        
        grid.activePole(best.x());
        
        return combo;
    }
    
    public static int measureGridValue(CheeseGrid grid, Team team) {
        int score = 0;
        
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                if (team == Team.RED && block.isRedMouse()) {
                    // x score is positive -- red wants blue
                    // and red mice on the right side
                    score += x;
                } else if (team == Team.BLUE && block.isBlueMouse()) {
                    // ... this is a simplistic scoring mechanism, has problems,
                    // but whatever, it's the easy cpu
                    // (in particular, it doesn't bother with escaped mice)
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
}
