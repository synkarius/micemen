package com.explosionduck.micemen.control.computer;

import com.explosionduck.micemen.calculation.ChoiceTree;
import com.explosionduck.micemen.calculation.SimulationFlatTree;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.command.grid.ProgressCommand;
import com.explosionduck.micemen.command.grid.SetHandCommand;
import com.explosionduck.micemen.control.ControllerType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.singleton;

public class TreeSearchComputerPlayer implements ComputerPlayer {

    private final GridCalculator calculator;
    private final CheeseGrid grid;
    private final int lookAhead;
    private final ChoiceTree choiceTree;

    public TreeSearchComputerPlayer(
            ExecutorService executorService,
            GridCalculator calculator,
            CheeseGrid grid,
            Team team,
            int lookAhead) {
        this.calculator = calculator;
        this.grid = grid;
        this.lookAhead = lookAhead;
        this.choiceTree = new SimulationFlatTree(executorService, calculator, grid, team, lookAhead);
    }

    @Override
    public Collection<Command> getCommands() throws CheeseException {
        if (!this.choiceTree.isReady()) {
            return singleton(this.choiceTree.process());
        }

        var best = this.choiceTree.getBestFuture(this.lookAhead);
        this.choiceTree.clear();

        Collection<Command> commands = new ArrayList<>();
        commands.add(new ProgressCommand(0, 0));
        commands.add(new SetHandCommand(this.grid, best.getX()));
        commands.add(new ColumnShiftCommand(this.calculator, best.getX(), best.getDirection()));

        this.grid.setActivePole(best.getX());

        return commands;
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.COMPUTER;
    }

    @Override
    public ComputerPlayerType getComputerPlayerType() {
        return ComputerPlayerType.TREE_SEARCH;
    }

    @Override
    public int getLookAhead() {
        return lookAhead;
    }
}
