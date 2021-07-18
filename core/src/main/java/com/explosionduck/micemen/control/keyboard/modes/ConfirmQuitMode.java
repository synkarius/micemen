package com.explosionduck.micemen.control.keyboard.modes;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.menu.QuitCommand;

import java.util.Collection;

import static java.util.Collections.singleton;

public class ConfirmQuitMode extends AbstractConfirmationMode {

    @Override
    protected Collection<Command> getCommandsForAffirmative() {
        return singleton(new QuitCommand());
    }

    @Override
    public ControlModeType getType() {
        return ControlModeType.CONFIRM_QUIT;
    }
}
