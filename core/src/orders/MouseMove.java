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
    
    private final int            ox;
    private final int            oy;
    
    public MouseMove(Mouse simMouse, int x, int y) {
        this.ox = x;
        this.oy = y;
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
            // applicableMouse = simMouse.getOriginal(grid.id());
            applicableMouse = (Mouse) grid.get(ox, oy);
            
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
            SimPoint destination = current.add(next);
            
            grid.recording().addMove(applicableMouse.team(), current.x, current.y, destination.x, destination.y);
            
            if (grid.isGraphical()) {
                if (next.x != 0)
                    Resource.lo2.play();
                else if (next.y != 0)
                    Resource.lo.play();
                
                if (destination.x == 0 || destination.x == grid.wMax())
                    applicableMouse.graphic(Graphic.UMBRELLA);
                else
                    grid.state().anim().walk(applicableMouse);
            }
            
            grid.switcH(current, destination);
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
