package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;

import static java.util.Collections.emptySet;

/**
 * Enables animation, sounds, etc.
 */
public class EnableEffectsCommand implements Command {

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        context.getGrid().makeGraphical();
        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.ENABLE_EFFECTS;
    }
}
