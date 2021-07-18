package com.explosionduck.micemen.command;

import com.explosionduck.micemen.control.Controller;

import java.util.Collection;

/**
 * Can't process load game on old {@link CommandExecutor} and {@link Controller}s. This gives
 * the load game command access to the new game's {@link CommandExecutor} and blue {@link Controller}.
 */
public interface NewGameBootstrapper {

    void addCommands(Collection<Command> commands);

    void setBlueController(Controller controller);
}
