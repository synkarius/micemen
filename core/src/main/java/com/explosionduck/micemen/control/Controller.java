package com.explosionduck.micemen.control;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.domain.CheeseException;

import java.util.Collection;

public interface Controller {

    Collection<Command> getCommands() throws CheeseException;

    ControllerType getControllerType();
}
