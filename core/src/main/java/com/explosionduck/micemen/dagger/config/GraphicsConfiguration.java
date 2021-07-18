package com.explosionduck.micemen.dagger.config;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.explosionduck.micemen.fx.DefaultMouseTexturesMap;
import com.explosionduck.micemen.fx.MouseTexturesMap;
import com.explosionduck.micemen.screens.game.GameScreen;
import com.explosionduck.micemen.screens.game.GameScreenRenderer;
import com.explosionduck.micemen.screens.title.TitleScreen;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class GraphicsConfiguration {

    @Provides
    @Singleton
    SpriteBatch provideSpriteBatch() {
        return new SpriteBatch();
    }

    @Provides
    @Singleton
    ShapeRenderer provideShapeRenderer(SpriteBatch batch) {
        ShapeRenderer shaper = new ShapeRenderer();
        shaper.setProjectionMatrix(batch.getProjectionMatrix());
        return shaper;
    }

    @Provides
    @Singleton
    BitmapFont provideBitmapFont() {
        BitmapFont font = new BitmapFont();
        font.setColor(Color.WHITE);
        return font;
    }

    @Provides
    @Singleton
    FitViewport provideFitViewport() {
        var camera = new OrthographicCamera(GameScreen.WIDTH, GameScreen.HEIGHT);
        return new FitViewport(GameScreen.WIDTH, GameScreen.HEIGHT, camera);
    }

    @Provides
    @Singleton
    MouseTexturesMap provideMouseTexturesMap() {
        return new DefaultMouseTexturesMap();
    }

    @Provides
    @Singleton
    GameScreenRenderer provideGameScreenRenderer(
            SpriteBatch batch,
            ShapeRenderer shaper,
            BitmapFont font,
            MouseTexturesMap mouseTexturesMap) {
        return new GameScreenRenderer(batch, shaper, font, mouseTexturesMap);
    }

    @Provides
    @Singleton
    TitleScreen provideTitleScreen(SpriteBatch batch, MouseTexturesMap mouseTexturesMap) {
        return new TitleScreen(batch, mouseTexturesMap);
    }
}
