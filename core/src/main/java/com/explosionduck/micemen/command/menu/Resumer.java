package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.control.keyboard.modes.ControlModeType;
import com.explosionduck.micemen.fx.MenuText;

import java.util.Collection;

import static java.util.Collections.singleton;

public interface Resumer {

    default Collection<Command> getCommandsToReturnToGameMode() {
        return singleton(new ChangeModeCommand(ControlModeType.GAME, MenuText.STANDARD_OPTIONS));
    }
}
