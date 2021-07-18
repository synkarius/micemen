package com.explosionduck.micemen.fx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

public final class Fonts {
    public static final BitmapFont LARGE;
    public static final BitmapFont NORMAL;

    static {
        // open sans: https://www.fontsquirrel.com/fonts/open-sans
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("opensans-font/OpenSans-Regular.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        NORMAL = generator.generateFont(parameter);
        parameter.size = 26;
        LARGE = generator.generateFont(parameter);
    }
}
