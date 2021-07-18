package com.explosionduck.micemen.control.keyboard.modes;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.menu.SaveGameCommand;

import java.util.Collection;

import static java.util.Collections.singleton;

public class ConfirmSaveMode extends AbstractConfirmationMode {

    @Override
    protected Collection<Command> getCommandsForAffirmative() {
        return singleton(new SaveGameCommand());
    }

    @Override
    public ControlModeType getType() {
        return ControlModeType.CONFIRM_SAVE;
    }
}
