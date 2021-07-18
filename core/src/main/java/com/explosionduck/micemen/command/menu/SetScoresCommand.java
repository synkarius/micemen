package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;

import static java.util.Collections.emptySet;

public class SetScoresCommand implements Command {

    private final BoardScorer scorer;

    public SetScoresCommand(BoardScorer scorer) {
        this.scorer = scorer;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        var scores = this.scorer.scores(context.getGrid(), false);
        context.setRedScore(scores.redBoardValue);
        context.setBlueScore(scores.blueScore);

        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.SET_SCORES;
    }
}
