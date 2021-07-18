package com.explosionduck.micemen.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.explosionduck.micemen.MainGame;
import com.explosionduck.micemen.dagger.config.DaggerPreLibGDXInitComponent;

public class DesktopLauncher {

    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Mice Men: Remix";

        // now with Dagger 2 injection!
        MainGame mainGame = DaggerPreLibGDXInitComponent.create().buildGame();

        new LwjglApplication(mainGame, config);
    }
}
