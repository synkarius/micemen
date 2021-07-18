package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Team;

import java.util.Collection;

import static java.util.Collections.singleton;

public class EvaluateBoardCommand implements Command {

    private final BoardScorer scorer;

    public EvaluateBoardCommand(BoardScorer scorer) {
        this.scorer = scorer;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        var scores = this.scorer.scores(context.getGrid(), true);
        int red = scores.redBoardValue;
        int blue = scores.blueBoardValue;

        if (red > blue) {
            return singleton(new SetMenuMessageCommand(declareBoardFavor(Team.RED, red - blue)));
        } else if (blue > red) {
            return singleton(new SetMenuMessageCommand(declareBoardFavor(Team.BLUE, blue - red)));
        } else {
            return singleton(new SetMenuMessageCommand("Fair Game"));
        }
    }

    @Override
    public CommandType getType() {
        return CommandType.EVALUATE_BOARD;
    }

    private String declareBoardFavor(Team team, int difference) {
        return "[" + team.toString() + " : " + difference + "]";
    }
}
