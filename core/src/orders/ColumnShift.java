package orders;

import java.util.List;

import com.mice.SceneGraph;

import control.Direction;
import model.Block;
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
        List<Block> column = grid.ctrl().columnCopy(x);
        int d = dir == Direction.UP ? PX_INCREMENT : -PX_INCREMENT;
        for (Block block : column)
            block.yOffset += d;
        
        // TODO: hand-slide up/down
        // TODO: sound
        
        pxCount += PX_INCREMENT;
        
        /** don't wait if it's not the on-screen grid */
        if (!grid.isGraphical())
            pxCount = pxMax;
        
        /* when done, update model */
        if (finished()) {
            grid.shift(dir, x);
            grid.activePole(x);
            grid.ctrl().recalculateMoves();
            grid.ctrl().orders().add(0, new PassTurn());
            
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
