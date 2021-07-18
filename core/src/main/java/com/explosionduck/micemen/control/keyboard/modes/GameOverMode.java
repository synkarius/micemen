package com.explosionduck.micemen.control.keyboard.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.menu.QuitCommand;
import com.explosionduck.micemen.command.menu.StartNewGameCommand;
import com.explosionduck.micemen.util.GameRestarter;

import java.util.Collection;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public class GameOverMode implements ControlMode {

    private final GameRestarter restart;

    public GameOverMode(GameRestarter restart) {
        this.restart = restart;
    }

    @Override
    public Collection<Command> generateCommands() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            return singleton(new StartNewGameCommand(restart));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            return singleton(new QuitCommand());
        } else {
            return emptySet();
        }
    }

    @Override
    public ControlModeType getType() {
        return ControlModeType.GAME_OVER;
    }
}
