package com.explosionduck.micemen.control.keyboard.modes;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.menu.StartNewGameCommand;
import com.explosionduck.micemen.util.GameRestarter;

import java.util.Collection;

import static java.util.Collections.singleton;

public class ConfirmNewGameMode extends AbstractConfirmationMode {

    private final GameRestarter restart;

    public ConfirmNewGameMode(GameRestarter restart) {
        this.restart = restart;
    }

    @Override
    protected Collection<Command> getCommandsForAffirmative() {
        return singleton(new StartNewGameCommand(restart));
    }

    @Override
    public ControlModeType getType() {
        return ControlModeType.CONFIRM_NEW_GAME;
    }
}
