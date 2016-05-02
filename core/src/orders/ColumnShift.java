package orders;

import control.Direction;
import graphical.Resource;
import graphical.SceneGraph;
import model.CheeseException;
import model.CheeseGrid;

public class ColumnShift implements IOrder {
    
    private int              pxCount;
    private int              pxMax;
    private int              x;
    private Direction        dir;
    private static final int PX_INCREMENT = 1;
    
    public ColumnShift(int x, Direction dir) {
        this.x = x;
        this.dir = dir;
        this.pxMax = SceneGraph.BLOCK_SIZE + 1;
    }
    
    public ColumnShift(ColumnShift other) {
        this(other.x, other.dir);
    }
    
    @Override
    public void execute(CheeseGrid grid) throws CheeseException {
        /** update y offsets for graphical shift */
        int d = dir == Direction.UP ? PX_INCREMENT : -PX_INCREMENT;
        grid.state().columnShifting = x;
        grid.state().yOffset += d;
        
        if (grid.isGraphical() && pxCount == 0)
            Resource.hi.play();
        
        pxCount += PX_INCREMENT;
        
        /** don't wait if it's not the on-screen grid */
        if (!grid.isGraphical())
            pxCount = pxMax;
        
        /* when done, update model */
        if (finished()) {
            grid.shift(dir, x);
            grid.activePole(x);
            grid.ctrl().recalculateMoves();
            grid.ctrl().orders().add(new PassTurn());
            
            grid.state().columnShifting = null;
            grid.state().yOffset = 0;
            
            grid.recording().shift(dir, x);
        }
    }
    
    @Override
    public boolean finished() {
        return pxCount == pxMax;
    }
    
    public int x() {
        return x;
    }
    
    public Direction dir() {
        return dir;
    }
    
    @Override
    public OrderType type() {
        return OrderType.COLUMN_SHIFT;
    }
}
