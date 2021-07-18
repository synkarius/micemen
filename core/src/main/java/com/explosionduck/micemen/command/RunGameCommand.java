package com.explosionduck.micemen.command;

import com.explosionduck.micemen.control.keyboard.modes.ControlModeType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;

import static java.util.Collections.emptySet;

public class RunGameCommand implements Command {

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        context.setControlModeType(ControlModeType.GAME);
        context.setText("<N>ew   <L>oad   <S>ave   <Q>uit");
        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.RUN_GAME;
    }
}
