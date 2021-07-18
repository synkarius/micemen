package com.explosionduck.micemen.command;

import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;

/**
 * Implementations should be FIFO.
 */
public interface CommandExecutor {

    void addCommand(Command command);

    void addCommands(Collection<Command> commands);

    boolean hasCommands();

    void executeNextCommand(GameContext gameContext);

    void executeAll(GameContext gameContext);
}
