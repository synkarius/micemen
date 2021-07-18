package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.control.keyboard.modes.ControlModeType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Team;

import java.util.Collection;

import static java.util.Collections.singleton;

public class SetGameOverCommand implements Command {

    private final Team winningTeam;

    public SetGameOverCommand(Team winningTeam) {
        this.winningTeam = winningTeam;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        return singleton(new ChangeModeCommand(ControlModeType.GAME_OVER,
                () -> this.winningTeam.name() + " wins! Play again? [Y/N]"));
    }

    @Override
    public CommandType getType() {
        return CommandType.SET_GAME_OVER;
    }
}
