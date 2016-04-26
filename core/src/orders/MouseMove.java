package orders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import graphical.GridGfx.Graphic;
import model.CheeseGrid;
import model.EmptyBlock;
import model.Mouse;
import model.SimPoint;

public class MouseMove implements IOrder {
    
    private static final int     DELAY = 15;
    
    private final Mouse          simMouse;
    private Mouse                applicableMouse;
    private final List<SimPoint> moves;
    private Iterator<SimPoint>   iter;
    private int                  counter;
    
    public MouseMove(Mouse simMouse) {
        this.simMouse = simMouse;
        this.moves = new ArrayList<>();
    }
    
    public void add(int xDif, int yDif) {
        moves.add(new SimPoint(xDif, yDif));
    }
    
    public void execute(CheeseGrid grid) {
        if (iter == null)
            iter = moves.iterator();
        
        if (applicableMouse == null || applicableMouse.gridID() != grid.id()) {
            applicableMouse = simMouse.getOriginal(grid.id());
            grid.state().reset(applicableMouse);
            applicableMouse.graphic(Graphic.WALK);
        }
        
        if (grid.isGraphical()) {
            // walking animation
            Integer frame = grid.state().getFrame(applicableMouse);
            Integer next = frame == 0 ? 1 : 0;
            grid.state().putFrame(applicableMouse, next);
            if (counter++ > DELAY) {
                counter = 0;
            } else {// and delay:
                return;
            }
        }
        
        if (iter.hasNext()) {
            SimPoint current = grid.get(applicableMouse);
            /**
             * not a "point" proper, but a +/- 1 x or y to add to the current
             * mouse position
             */
            SimPoint next = iter.next();
            EmptyBlock empty = (EmptyBlock) grid.get(current.x() + next.x(), current.y() + next.y());
            
            grid.switcH(applicableMouse, empty);
        }
    }
    
    public boolean finished() {
        return !iter.hasNext();
    }
    
    public SimPoint consolidate() {
        return moves.stream().reduce(new SimPoint(0, 0), SimPoint::add);
    }
    
    @Override
    public OrderType type() {
        return OrderType.MOUSE_MOVE;
    }
}
