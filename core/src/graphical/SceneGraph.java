package graphical;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import model.Block;
import model.CheeseGrid;

public class SceneGraph {   
    
    public static final int BLOCK_SIZE = 25;
    public static final int X_OFFSET   = 50;
    public static final int Y_OFFSET   = 100;
    
    public SceneGraph() {
    }
    
    public static int flipY(CheeseGrid grid, int y) {
        return Math.abs(grid.height() - y - 1);
    }
    
    public static void drawGrid(CheeseGrid grid, SpriteBatch batch) {
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                
                TextureRegion region = null;
                int _y = flipY(grid, y);
                
                if (block.isMouse()) {
                    region = block.isRedMouse() ? block.graphic().red() : block.graphic().blue();
                } else if (block.isCheese()) {
                    region = GridGfx.cheese;
                } else {
                    /** no need to draw empty blocks */
                    continue;
                }
                
                batch.draw(region, x * BLOCK_SIZE + X_OFFSET, _y * BLOCK_SIZE + Y_OFFSET);
            }
        }
    }
}
