package orders;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import graphical.Resource;
import graphical.Resource.Graphic;
import model.CheeseGrid;
import model.Mouse;
import model.SimPoint;

public class MouseMove implements IOrder {
    
    private static final int     DELAY = 20;
    
    private final Mouse          simMouse;
    private Mouse                applicableMouse;
    private final List<SimPoint> moves;
    private Iterator<SimPoint>   iter;
    private int                  counter;
    boolean                      finished;
    
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
            grid.state().anim().reset(applicableMouse);
        }
        
        if (grid.isGraphical()) {
            if (counter++ > DELAY) {
                counter = 0;
            } else {// delay:
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
            int newX = current.x() + next.x();
            
            if (grid.isGraphical()) {
                if (next.x() != 0)
                    Resource.lo2.play();
                else if (next.y() != 0)
                    Resource.lo.play();
                
                if (newX == 0 || newX == grid.wMax())
                    applicableMouse.graphic(Graphic.UMBRELLA);
                else
                    grid.state().anim().walk(applicableMouse);
            }
            
            grid.switcH(current, current.add(next));
        }
        
        finished = !iter.hasNext();
        
        if (grid.isGraphical() && finished)
            grid.state().anim().reset(applicableMouse);
    }
    
    public boolean finished() {
        return finished;
    }
    
    public SimPoint consolidate() {
        return moves.stream().reduce(new SimPoint(0, 0), SimPoint::add);
    }
    
    @Override
    public OrderType type() {
        return OrderType.MOUSE_MOVE;
    }
}
