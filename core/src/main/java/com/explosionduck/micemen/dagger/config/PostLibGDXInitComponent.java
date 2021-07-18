package com.explosionduck.micemen.dagger.config;

import com.badlogic.gdx.utils.viewport.FitViewport;
import com.explosionduck.micemen.screens.game.GameScreenRenderer;
import com.explosionduck.micemen.screens.title.TitleScreen;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        GraphicsConfiguration.class
})
public interface PostLibGDXInitComponent {

    GameScreenRenderer buildGameScreenRenderer();

    FitViewport buildFitViewport();

    TitleScreen buildTitleScreen();
}
