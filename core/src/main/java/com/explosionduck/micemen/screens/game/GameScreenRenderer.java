package com.explosionduck.micemen.screens.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.Point;
import com.explosionduck.micemen.domain.blocks.Block;
import com.explosionduck.micemen.domain.blocks.Mouse;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.fx.MouseGraphic;
import com.explosionduck.micemen.fx.MouseTexturesMap;
import com.explosionduck.micemen.fx.Textures;
import com.explosionduck.micemen.screens.AbstractScreen;

import static com.explosionduck.micemen.screens.game.GameScreen.*;

public class GameScreenRenderer {

    private final SpriteBatch batch;
    private final ShapeRenderer shaper;
    private final BitmapFont font;
    private final MouseTexturesMap mouseTexturesMap;

    public GameScreenRenderer(
            SpriteBatch batch, ShapeRenderer shaper, BitmapFont font, MouseTexturesMap mouseTexturesMap) {
        this.batch = batch;
        this.shaper = shaper;
        this.font = font;
        this.mouseTexturesMap = mouseTexturesMap;
    }

    public void draw(GameContext context) {
        //==================
        batch.begin();
        batch.draw(Textures.BG, AbstractScreen.X_OFFSET, AbstractScreen.Y_OFFSET);
        Point escapee = this.drawGrid(context, this.batch);

        batch.end();

        shaper.begin(ShapeRenderer.ShapeType.Filled);
        this.drawBlockers();
        shaper.end();

        batch.begin();
        this.drawControls(context);
        batch.end();

        //================
        shaper.begin(ShapeRenderer.ShapeType.Filled);
        this.drawBoxes();
        if (context.getThinkingTotal() != 0) {
            this.drawThinking(context);
        }
        shaper.end();

        //================

        batch.begin();
        this.drawText(context);
        if (context.getThinkingTotal() != 0) {
//            font.draw(batch, "Thinking ...", MENU_TEXT_X_OFFSET, 200);
        }
        if (escapee != null) {
            this.drawEscapee(escapee);
        }
        batch.end();
    }

    /**
     * returns escapee mouse if there is one
     */
    private Point drawGrid(GameContext context, SpriteBatch batch) {
        var grid = context.getGrid();
        Point escapee = null;

        for (int x = 0; x < grid.getWidth(); x++) {
            int yOffset = 0;
//            if (grid.getState().columnShifting != null && grid.getState().columnShifting == x) {
            if (context.getColumnShifting() != null && context.getColumnShifting() == x) {
//                yOffset = grid.getState().yOffset;
                yOffset = context.getYOffset();
            }

            for (int y = 0; y < grid.getHeight(); y++) {
                Block block = grid.getBlockAt(x, y);

                TextureRegion region;
                int _y = flipY(grid, y);

                if (block.isMouse()) {
                    Mouse mouse = (Mouse) block;
                    MouseGraphic graphic = mouse.getGraphic();
                    switch (graphic) {
                        case EAT1:
//                            graphic = grid.getState().getAnim().getFrameForEatingMouse((Mouse) block);
                            graphic = context.getAnimationFrameMap().getFrameForEatingMouse(mouse);
                            break;
                        case MUSCLE1:
//                            graphic = grid.getState().getAnim().getFrameForMuscle((Mouse) block);
                            graphic = context.getAnimationFrameMap().getFrameForMuscle(mouse);
                            break;
                        case FALL:
//                            yOffset = (int) grid.getState().getAnim().mouseFallOffset((Mouse) block);
                            yOffset = (int) context.getAnimationFrameMap().mouseFallOffset(mouse);
                            escapee = new Point(x, _y * AbstractScreen.BLOCK_SIZE + AbstractScreen.Y_OFFSET + yOffset);
                            continue;
                        default:
                            break;
                    }
                    region = this.mouseTexturesMap.getTextureRegion(graphic, mouse.getTeam());
//                    region = block.isRedMouse() ? graphic.red() : graphic.blue();

                } else if (block.isCheese()) {
                    region = Textures.CHEESE;
                } else {
                    /* no need to draw empty blocks */
                    continue;
                }

                batch.draw(region, x * AbstractScreen.BLOCK_SIZE + AbstractScreen.X_OFFSET, _y * AbstractScreen.BLOCK_SIZE + AbstractScreen.Y_OFFSET + yOffset);
            }
        }

        return escapee;
    }

    /**
     * must draw escapee last so that it can be drawn over black blocks -- black
     * blocks are necessary to make columnshifting look smooth
     *
     * @param escapee
     */
    private void drawEscapee(Point escapee) {
        int x = escapee.x * AbstractScreen.BLOCK_SIZE + AbstractScreen.X_OFFSET;
        TextureRegion fall = this.mouseTexturesMap.getTextureRegion(MouseGraphic.FALL, escapee.x != 0 ? Team.RED : Team.BLUE);
//        TextureRegion fall = escapee.x != 0 ? Textures.MouseGraphic.FALL.red() : Textures.MouseGraphic.FALL.blue();
        batch.draw(fall, x, escapee.y);
    }

    private void drawBoxes() {
        // menu box
        shaper.setColor(Color.PURPLE);
        shaper.rect(AbstractScreen.X_OFFSET, 0, AbstractScreen.BLOCK_SIZE * 21, 30);
    }

    private void drawBlockers() {
        // blockers
        shaper.setColor(Color.BLACK);
        shaper.rect(0, HEIGHT - BLOCKER_TOP, WIDTH, BLOCKER_TOP);
        shaper.rect(0, 0, WIDTH, BLOCKER_BOTTOM);
    }

    private void drawThinking(GameContext context) {

        // menu box
        shaper.setColor(Color.PURPLE);
        shaper.rect(AbstractScreen.X_OFFSET, 0, AbstractScreen.BLOCK_SIZE * 21, 30);
//        if (grid.getState().thinkingReady > 0) {
        if (context.getThinkingReady() > 0) {
            shaper.setColor(Color.GREEN);
//            float percent = (float) (grid.getState().thinkingReady / grid.getState().thinkingTotal);
            float percent = (float) (context.getThinkingReady() / context.getThinkingTotal());
            shaper.rect(AbstractScreen.X_OFFSET, 0, AbstractScreen.BLOCK_SIZE * 21 * percent, 30);
        }

    }

    private void drawControls(GameContext context) {
        var grid = context.getGrid();

        for (int p = 0; p < grid.getPoles().length; p++)
            if (grid.getPoles()[p])
                batch.draw(Textures.POLE, AbstractScreen.X_OFFSET + POLE_OFFSET_X + p * AbstractScreen.BLOCK_SIZE, POLE_HEIGHT_Y);

        float yOffsetForShift = 0;
//        boolean isShifting = grid.getState().columnShifting != null;
        boolean isShifting = context.getColumnShifting() != null;
        if (isShifting) {
//            yOffsetForShift = (float) (grid.getState().yOffset / 4.0);
            yOffsetForShift = (float) (context.getYOffset() / 4.0);
        }

        if (grid.getRedHand() != null) {
            if (isShifting) {
//                grid.getState().redHandYOffset = yOffsetForShift;
//                grid.getState().blueHandYOffset = 0;
                context.setRedHandYOffset(yOffsetForShift);
                context.setBlueHandYOffset(0);
            }
            batch.draw(Textures.RED_HAND, AbstractScreen.X_OFFSET + HAND_OFFSET_X + grid.getRedHand() * AbstractScreen.BLOCK_SIZE,
//                    POLE_HEIGHT_Y + HAND_OFFSET_Y + grid.getState().redHandYOffset);
                    POLE_HEIGHT_Y + HAND_OFFSET_Y + context.getRedHandYOffset());
        } else if (grid.getBlueHand() != null) {
            if (isShifting) {
//                grid.getState().redHandYOffset = 0;
//                grid.getState().blueHandYOffset = yOffsetForShift;
                context.setRedHandYOffset(0);
                context.setBlueHandYOffset(yOffsetForShift);
            }
            batch.draw(Textures.BLUE_HAND, AbstractScreen.X_OFFSET + HAND_OFFSET_X + grid.getBlueHand() * AbstractScreen.BLOCK_SIZE,
//                    POLE_HEIGHT_Y + HAND_OFFSET_Y + grid.getState().blueHandYOffset);
                    POLE_HEIGHT_Y + HAND_OFFSET_Y + context.getBlueHandYOffset());
        }

        if (grid.getVerbotenColumn() != null) {
            batch.draw(Textures.VERBOTEN_X, AbstractScreen.X_OFFSET + THE_X_X_OFFSET + grid.getVerbotenColumn() * AbstractScreen.BLOCK_SIZE, POLE_HEIGHT_Y + HAND_OFFSET_Y);
        }
    }

    private void drawText(GameContext context) {
        font.draw(batch, context.getRedScore(), RED_SCORE_OFFSET_X, MENU_TEXT_Y);
        font.draw(batch, context.getBlueScore(), BLUE_SCORE_OFFSET_X, MENU_TEXT_Y);

        if (context.getText() != null)
            font.draw(batch, context.getText(), MENU_TEXT_X_OFFSET, MENU_TEXT_Y);
        if (context.getMessage() != null)
            font.draw(batch, context.getMessage(), MENU_MESSAGE_X_OFFSET, MENU_TEXT_Y);
    }

    private int flipY(CheeseGrid grid, int y) {
        return Math.abs(grid.getHeight() - y - 1);
    }
}
