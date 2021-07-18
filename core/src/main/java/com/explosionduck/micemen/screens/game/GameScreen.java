package com.explosionduck.micemen.screens.game;

import com.explosionduck.micemen.screens.AbstractScreen;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.screens.ScreenType;

public class GameScreen extends AbstractScreen {

    public static final float HEIGHT = 480;
    public static final float WIDTH = 640;
    public static final float BLOCKER_TOP = 55, BLOCKER_BOTTOM = 100;
    public static final float POLE_OFFSET_X = 7.5f;
    public static final int POLE_HEIGHT_Y = 50;

    public static final int RED_SCORE_OFFSET_X = X_OFFSET + 5, BLUE_SCORE_OFFSET_X = (int) (WIDTH - X_OFFSET - 25);
    public static final int MENU_TEXT_X_OFFSET = X_OFFSET + 30, MENU_MESSAGE_X_OFFSET = (int) (WIDTH - X_OFFSET - 170);
    public static final int MENU_TEXT_Y = 20;
    public static final float HAND_OFFSET_X = 7.5f, HAND_OFFSET_Y = 10;
    public static final float THE_X_X_OFFSET = 5.5f;

    //
    private final GameScreenRenderer renderer;
    private final GameContext gameContext;

    public GameScreen(
            GameScreenRenderer renderer,
            GameContext gameContext) {
        this.renderer = renderer;
        this.gameContext = gameContext;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public ScreenType getType() {
        return ScreenType.GAME;
    }

    @Override
    public void draw() {
        this.renderer.draw(this.gameContext);
    }


}
