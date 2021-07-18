package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.command.grid.PassTurnCommand;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Team;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Determines which player goes first, either after loading a saved game or starting a new game.
 */
public class InitialTurnCommand implements Command {

    private final Team team;

    public InitialTurnCommand(Team team) {
        this.team = team;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        var commands = new ArrayList<Command>();

//        commands.add(new ChangeModeCommand(ControlModeType.GAME, MenuText.STANDARD_OPTIONS));
        commands.add(0, new PassTurnCommand());

        if (this.team == null) {
            var firstTeam = Team.RED;
            if (Math.random() > .5) {
                firstTeam = Team.BLUE;
                commands.add(0, new PassTurnCommand());
            }
            commands.add(new SetMenuMessageCommand(firstTeam.name() + " first"));
        } else {
            // TODO: is this a bug? seems like this should be based on the active team
            //  nope, this was also called after choosing a human opponent or a cpu difficulty mode
            commands.add(0, new PassTurnCommand());
        }

        return commands;
    }

    @Override
    public CommandType getType() {
        return CommandType.INITIAL_TURN;
    }
}
