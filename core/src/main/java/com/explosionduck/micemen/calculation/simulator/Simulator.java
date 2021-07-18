package com.explosionduck.micemen.calculation.simulator;

import com.explosionduck.micemen.calculation.BoardGenerator;
import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.calculation.noop.NoOpGameContext;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.command.DefaultCommandExecutor;
import com.explosionduck.micemen.command.grid.PassTurnCommand;
import com.explosionduck.micemen.control.Controller;
import com.explosionduck.micemen.control.computer.BasicComputerPlayer;
import com.explosionduck.micemen.control.computer.TreeSearchComputerPlayer;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.Team;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import static java.util.function.Predicate.not;

/**
 * Runs 1000 games, prints results.
 */
public class Simulator {

    private final ExecutorService executorService;
    private final GridCalculator gridCalculator;
    private final BoardGenerator boardGenerator;
    private final BoardScorer boardScorer;

    public Simulator(
            ExecutorService executorService,
            GridCalculator gridCalculator,
            BoardGenerator boardGenerator,
            BoardScorer boardScorer) {
        this.executorService = executorService;
        this.gridCalculator = gridCalculator;
        this.boardGenerator = boardGenerator;
        this.boardScorer = boardScorer;
    }

    public void simulate() throws CheeseException {
        final long totalStart = java.lang.System.currentTimeMillis();
        int redWins = 0;
        int blueWins = 0;
        int redStartedAhead = 0;
        int blueStartedAhead = 0;

        allGames: for (int i = 0; i < 1000; i++) {
            // set up game
            var grid = CheeseGrid.getNewDefault();
            grid.setActiveTeam(Team.RED);
            this.boardGenerator.fillGrid(grid);
            var gameContext = new NoOpGameContext(grid);
            var commandExecutor = new DefaultCommandExecutor();
            commandExecutor.addCommands(this.gridCalculator.calculateNewCommands(grid));
            commandExecutor.executeAll(gameContext);

            // keep a tally of how many times red started ahead vs blue
            var starting = boardScorer.scores(grid, true);
            if (starting.redBoardValue > starting.blueBoardValue) {
                redStartedAhead++;
            } else if (starting.blueBoardValue > starting.redBoardValue) {
                blueStartedAhead++;
            }

            var redController = new BasicComputerPlayer(this.gridCalculator, grid, Team.RED);
            var blueController = new TreeSearchComputerPlayer(executorService, this.gridCalculator, grid, Team.BLUE, 3);
            int redScore;
            int blueScore;
            int moveCount = 0;

            commandExecutor.addCommand(new PassTurnCommand());
            if (Math.random() < .5) {
                commandExecutor.addCommand(new PassTurnCommand());
            }
            commandExecutor.executeAll(gameContext);

            long start = java.lang.System.currentTimeMillis();

            singleGameExecutionLoop: while (true) {
                Controller controller = grid.getActiveTeam() == Team.RED ? redController : blueController;
                Collection<Command> commands = controller.getCommands();
                commandExecutor.addCommands(commands);
                commandExecutor.executeAll(gameContext);

                if (commands.stream()
                        .map(Command::getType)
                        .anyMatch(not(CommandType.PROGRESS::equals))) {
                    BoardScorer.Scores scores = boardScorer.scores(grid, false);
                    redScore = scores.redScore;
                    blueScore = scores.blueScore;

                    if (++moveCount > 200) { // discard deadlocked games
                        continue allGames;
                    }

                    if (redScore == grid.getMicePerTeam() || blueScore == grid.getMicePerTeam())
                        break singleGameExecutionLoop;
                }
            }

            long delta = System.currentTimeMillis() - start;

            System.out.println("game: " + i + " moves: " + moveCount + " ; red: " + redScore + " ; blue: "
                    + blueScore + " ; time(ms): " + delta);

            if (redScore == grid.getMicePerTeam() || blueScore == grid.getMicePerTeam()) {
                if (redScore > blueScore) {
                    redWins++;
                } else if (blueScore > redScore) {
                    blueWins++;
                }
            }
        }

        System.out.println("total time: " + (System.currentTimeMillis() - totalStart) +
                " red: " + redWins + " blue: " + blueWins +
                " headstarts:: red: " + redStartedAhead + " blue: " + blueStartedAhead);
    }
}
