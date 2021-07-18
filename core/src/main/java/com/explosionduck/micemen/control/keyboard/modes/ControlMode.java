package com.explosionduck.micemen.control.keyboard.modes;

import com.explosionduck.micemen.command.Command;

import java.util.Collection;

public interface ControlMode {

    Collection<Command> generateCommands();

    ControlModeType getType();
}
