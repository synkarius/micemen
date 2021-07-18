package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.util.GameRestarter;

import java.util.Collection;

import static java.util.Collections.emptySet;

public class StartNewGameCommand implements Command {

    private final GameRestarter restart;

    public StartNewGameCommand(GameRestarter restart) {
        this.restart = restart;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        this.restart.restartGame(null);
        // TODO: Should there be more here? Can I move the entire setup code stuff to here?
        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.START_NEW_GAME;
    }
}
