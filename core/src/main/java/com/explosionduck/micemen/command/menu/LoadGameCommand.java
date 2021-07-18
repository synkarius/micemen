package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.control.computer.BasicComputerPlayer;
import com.explosionduck.micemen.control.computer.TreeSearchComputerPlayer;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.io.FileIO;
import com.explosionduck.micemen.util.GameRestarter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.emptySet;

public class LoadGameCommand implements Command, Resumer {

    private final GameRestarter restart;
    private final BoardScorer boardScorer;
    private final ExecutorService executorService;
    private final GridCalculator gridCalculator;

    public LoadGameCommand(
            GameRestarter restart,
            BoardScorer boardScorer,
            ExecutorService executorService,
            GridCalculator gridCalculator) {
        this.restart = restart;
        this.boardScorer = boardScorer;
        this.executorService = executorService;
        this.gridCalculator = gridCalculator;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        var loadedGame = FileIO.loadFromSave();
        var bootstrapper = this.restart.restartGame(loadedGame);

        // set up opponent from saved game
        if (loadedGame.getOpponentWasCPU()) {
            var blue = loadedGame.getOpponentLevel() == 1
                    ? new BasicComputerPlayer(gridCalculator, loadedGame, Team.BLUE)
                    : new TreeSearchComputerPlayer(executorService, gridCalculator, loadedGame, Team.BLUE, loadedGame.getOpponentLevel());
            bootstrapper.setBlueController(blue);
        }

        var commands = new ArrayList<>(getCommandsToReturnToGameMode());
        commands.add(new SetScoresCommand(this.boardScorer));
        bootstrapper.addCommands(commands);

        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.LOAD_GAME;
    }
}
