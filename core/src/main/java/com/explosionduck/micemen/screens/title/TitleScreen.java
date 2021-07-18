package com.explosionduck.micemen.screens.title;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.explosionduck.micemen.domain.Point;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.fx.Fonts;
import com.explosionduck.micemen.fx.MouseGraphic;
import com.explosionduck.micemen.fx.MouseTexturesMap;
import com.explosionduck.micemen.fx.Textures;
import com.explosionduck.micemen.screens.AbstractScreen;
import com.explosionduck.micemen.screens.ScreenType;

import java.util.ArrayList;
import java.util.List;

public class TitleScreen extends AbstractScreen {

    static final int ACROSS = 21;
    static final int DOWN = 13;
    private static final int SPEED = 25;
    private static final int DRAW_TITLE = 150;
    private static final int CREDITS_1 = DRAW_TITLE + 50;
    private static final int CREDITS_2 = CREDITS_1 + 50;
    //
    private final SpriteBatch batch;
    private final MouseTexturesMap mouseTexturesMap;
    private final List<Point> cheese;
    private final List<TitleMouse> mice;
    //
    private int frame;
    private boolean finished;

    public TitleScreen(SpriteBatch batch, MouseTexturesMap mouseTexturesMap) {
        this.batch = batch;
        this.mouseTexturesMap = mouseTexturesMap;
        this.cheese = new ArrayList<>();
        this.mice = new ArrayList<>();
        this.setup();
    }

    private void setup() {
        // cheese border setup
        for (int x = 0; x < ACROSS; x++) {
            cheese.add(new Point(x, 0));
            cheese.add(new Point(x, DOWN - 1));
        }
        for (int y = 1; y < DOWN - 1; y++) {
            cheese.add(new Point(0, y));
            cheese.add(new Point(ACROSS - 1, y));
        }
        // falling mice setup
        for (int i = 0; i < 6; i++) {
            for (Team team : Team.values()) {
                int next = 0;
                int randomY = (int) Math.round(DOWN + Math.random() * 5);
                int x = team == Team.RED ? i + 1 : 20 - i - 1;

                TitleMouse controller = new TitleMouse(x, randomY, team, MouseGraphic.STAND);
                controller.changes.put(next++ * SPEED, TitleScreenChange.UMBRELLA);
                for (int n = 0; n < 17; n++)
                    controller.changes.put(next++ * SPEED, TitleScreenChange.DOWN);
                controller.changes.put(next++ * SPEED, TitleScreenChange.FACE_CAMERA);
                mice.add(controller);
            }
        }
    }

    public void draw() { // TODO: this method does too much -- break it up
        boolean skip = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        if (skip) {
            finished = true;
            return;
        }

        batch.begin();

        for (Point c : cheese)
            batch.draw(Textures.CHEESE, c.x * BLOCK_SIZE + X_OFFSET,
                    c.y * BLOCK_SIZE + Y_OFFSET);

        for (TitleMouse mouse : mice) {
            mouse.play(frame);
            Sprite sprite = new Sprite(this.mouseTexturesMap.getTextureRegion(mouse.graphic(), mouse.team()));
            sprite.setAlpha(mouse.alpha);
            sprite.setPosition(mouse.x * BLOCK_SIZE + X_OFFSET,
                    mouse.y * BLOCK_SIZE + Y_OFFSET);
            sprite.draw(batch);
        }

        if (frame > DRAW_TITLE)
            Fonts.LARGE.draw(batch, "Mice Men: Remix", 220, 300);
        if (frame > CREDITS_1)
            Fonts.NORMAL.draw(batch, "by synkarius", 275, 250);
        if (frame > CREDITS_2)
            Fonts.NORMAL.draw(batch, "original by William Soleau", 235, 220);

        finished = frame++ == 25 * SPEED;

        batch.end();
    }

    @Override
    public boolean isActive() {
        return !finished;
    }

    @Override
    public ScreenType getType() {
        return ScreenType.TITLE;
    }

    public boolean isFinished() { // TODO: don't need to clear lists every time this is called
        if (finished) {
            cheese.clear();
            mice.clear();
        }
        return finished;
    }

}
