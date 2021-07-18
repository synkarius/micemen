package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.io.FileIO;

import java.util.Collection;

public class SaveGameCommand implements Command, Resumer {

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        FileIO.saveGame(context.getGrid(), context.getBlueController());
        return getCommandsToReturnToGameMode();
    }

    @Override
    public CommandType getType() {
        return CommandType.SAVE_GAME;
    }
}
