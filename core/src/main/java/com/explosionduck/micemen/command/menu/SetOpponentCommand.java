package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.OpponentMode;

import java.util.Collection;

import static java.util.Collections.emptySet;

public class SetOpponentCommand implements Command {

    private final OpponentMode opponentMode;

    public SetOpponentCommand(OpponentMode opponentMode) {
        this.opponentMode = opponentMode;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        context.setOpponentMode(this.opponentMode);
        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.SET_OPPONENT;
    }
}
