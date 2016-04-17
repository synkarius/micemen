package graphical;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import model.Block;
import model.CheeseGrid;

public class SceneGraph {
    
    public static final int   BLOCK_SIZE         = 25;
    public static final int   X_OFFSET           = 55;
    public static final int   Y_OFFSET           = 100;
    public static final float WIDTH              = 640, HEIGHT = 480;
    public static final float BLOCKER_TOP        = 55, BLOCKER_BOTTOM = 100;
    public static final float POLE_OFFSET_X      = (float) 7.5;
    public static final int   POLE_HEIGHT_Y      = 50;
    public static final int   RED_SCORE_OFFSET_X = X_OFFSET + 5, BLUE_SCORE_OFFSET_X = (int) (WIDTH - X_OFFSET - 20);
    public static final int   MENU_TEXT_X_OFFSET = X_OFFSET + 30,
            MENU_MESSAGE_X_OFFSET = (int) (WIDTH - X_OFFSET - 170);
    public static final int   MENU_TEXT_Y        = 20;
    public static final float   HAND_OFFSET_X      = (float) 7.5, HAND_OFFSET_Y = 10;
    
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
    
    public static void drawBoxes(ShapeRenderer shaper) {
        shaper.begin(ShapeType.Filled);
        // blockers
        shaper.setColor(Color.BLACK);
        shaper.rect(0, HEIGHT - BLOCKER_TOP, WIDTH, BLOCKER_TOP);
        shaper.rect(0, 0, WIDTH, BLOCKER_BOTTOM);
        // menu box
        shaper.setColor(Color.PURPLE);
        shaper.rect(X_OFFSET, 0, BLOCK_SIZE * 21, 30);
        shaper.end();
    }
    
    public static void drawControls(CheeseGrid grid, SpriteBatch batch) {
        for (int p = 0; p < grid.poles().length; p++)
            if (grid.poles()[p])
                batch.draw(GridGfx.pole, X_OFFSET + POLE_OFFSET_X + p * BLOCK_SIZE, POLE_HEIGHT_Y);
        
        if (grid.redHand() != null)
            batch.draw(GridGfx.redHand, X_OFFSET + HAND_OFFSET_X + grid.redHand() * BLOCK_SIZE,
                    POLE_HEIGHT_Y + HAND_OFFSET_Y);
        else if (grid.blueHand() != null)
            batch.draw(GridGfx.blueHand, X_OFFSET + HAND_OFFSET_X + grid.blueHand() * BLOCK_SIZE,
                    POLE_HEIGHT_Y + HAND_OFFSET_Y);
        
    }
    
    public static void drawText(CheeseGrid grid, SpriteBatch batch, BitmapFont font) {
        font.draw(batch, "" + grid.state().menu().redScore, RED_SCORE_OFFSET_X, MENU_TEXT_Y);
        font.draw(batch, "" + grid.state().menu().blueScore, BLUE_SCORE_OFFSET_X, MENU_TEXT_Y);
        
        if (grid.state().menu().text != null)
            font.draw(batch, grid.state().menu().text, MENU_TEXT_X_OFFSET, MENU_TEXT_Y);
        if (grid.state().menu().message != null)
            font.draw(batch, grid.state().menu().message, MENU_MESSAGE_X_OFFSET, MENU_TEXT_Y);
    }
}
