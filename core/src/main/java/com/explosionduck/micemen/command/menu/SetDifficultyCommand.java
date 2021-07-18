package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.Difficulty;
import com.explosionduck.micemen.control.computer.BasicComputerPlayer;
import com.explosionduck.micemen.control.computer.TreeSearchComputerPlayer;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Team;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.emptySet;

public class SetDifficultyCommand implements Command {

    private final ExecutorService executorService;
    private final GridCalculator calculator;
    private final CheeseGrid grid;
    private final Difficulty difficulty;

    public SetDifficultyCommand(
            ExecutorService executorService,
            GridCalculator calculator,
            CheeseGrid grid,
            Difficulty difficulty) {
        this.executorService = executorService;
        this.calculator = calculator;
        this.grid = grid;
        this.difficulty = difficulty;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        context.setDifficulty(this.difficulty);
        context.setBlueController(switch (this.difficulty) {
            case EASY -> new BasicComputerPlayer(calculator, grid, Team.BLUE);
            case MEDIUM -> new TreeSearchComputerPlayer(executorService, calculator, grid, Team.BLUE, 2);
            case HARD -> new TreeSearchComputerPlayer(executorService, calculator, grid, Team.BLUE, 3);
        });
        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.SET_DIFFICULTY;
    }
}
