package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;

import static java.util.Collections.emptySet;

public class SetMenuMessageCommand implements Command {

    private final String message;

    public SetMenuMessageCommand(String message) {
        this.message = message;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        context.setMessage(message);
        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.SET_MENU_MESSAGE;
    }
}
