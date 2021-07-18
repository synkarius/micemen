package com.explosionduck.micemen.command;

import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;

public interface Command {

    Collection<Command> execute(GameContext context) throws CheeseException;

    default boolean isComplete() {
        return true;
    }

    CommandType getType();
}
