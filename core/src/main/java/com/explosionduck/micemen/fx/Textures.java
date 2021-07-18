package com.explosionduck.micemen.fx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.explosionduck.micemen.screens.game.GameScreen;

public final class Textures {

    private static final Texture CHEESE_TEX = new Texture("cheese.png");
    private static final Texture X_TEX = new Texture("x.png");
    private static final Texture RED_HAND_TEX = new Texture("redhand.png");
    private static final Texture BLUE_HAND_TEX = new Texture("bluehand.png");
    private static final Texture POLE_TEX = new Texture("pole.png");
    private static final Texture BG_TEX = new Texture("bg.png");
    static final Texture REDS_TEX = new Texture("redmice.png");
    static final Texture BLUES_TEX = new Texture("bluemice.png");

    public static final TextureRegion CHEESE = new TextureRegion(CHEESE_TEX, 0, 0, GameScreen.BLOCK_SIZE,
            GameScreen.BLOCK_SIZE);
    public static final TextureRegion VERBOTEN_X = new TextureRegion(X_TEX, 0, 0, GameScreen.BLOCK_SIZE,
            GameScreen.BLOCK_SIZE);
    public static final TextureRegion RED_HAND = new TextureRegion(RED_HAND_TEX, 0, 0, GameScreen.BLOCK_SIZE,
            GameScreen.BLOCK_SIZE);
    public static final TextureRegion BLUE_HAND = new TextureRegion(BLUE_HAND_TEX, 0, 0, GameScreen.BLOCK_SIZE,
            GameScreen.BLOCK_SIZE);
    public static final TextureRegion POLE = new TextureRegion(POLE_TEX, 0, 0, 10, 50);
    public static final TextureRegion BG = new TextureRegion(BG_TEX);


    static {
        BG_TEX.setWrap(Texture.TextureWrap.MirroredRepeat, Texture.TextureWrap.MirroredRepeat);
        BG.setRegion(0, 0, GameScreen.BLOCK_SIZE * 21, GameScreen.BLOCK_SIZE * 13);
    }

}
