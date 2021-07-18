package com.explosionduck.micemen.control.computer;

import com.explosionduck.micemen.calculation.Analysis;
import com.explosionduck.micemen.calculation.SimulationNode;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.command.grid.SetHandCommand;
import com.explosionduck.micemen.control.ControllerType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.Team;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

public class BasicComputerPlayer implements ComputerPlayer {

    protected final CheeseGrid grid;
    protected final Team team;
    protected final GridCalculator calculator;

    public BasicComputerPlayer(GridCalculator calculator, CheeseGrid grid, Team team) {
        this.calculator = calculator;
        this.grid = grid;
        this.team = team;
    }

    @Override
    public Collection<Command> getCommands() throws CheeseException {
        SimulationNode best = null;
        var results = this.analyzeChoices(calculator.getChoices(grid), grid, team);
        for (SimulationNode result : results) {
            if (best == null || result.getValue() > best.getValue()) {
                best = result;
            }
        }

        if (best == null) {
            throw new CheeseException("No choices available.", grid);
        }

        Collection<Command> commands = new ArrayList<>();
        commands.add(new SetHandCommand(grid, best.getX()));
        commands.add(new ColumnShiftCommand(calculator, best.getX(), best.getDirection()));

        // TODO: this is an odd choice if we're using the command pattern - maybe should turn this into a command?
        grid.setActivePole(best.getX());

        return commands;
    }

    @Override
    public int getLookAhead() {
        return 1;
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.COMPUTER;
    }

    @Override
    public ComputerPlayerType getComputerPlayerType() {
        return ComputerPlayerType.BASIC;
    }

    private Collection<SimulationNode> analyzeChoices(ColumnShiftCommand[] choices, CheeseGrid grid, Team team) {
        return Arrays.stream(choices)
                .filter(Objects::nonNull)
                .map(choice -> this.analyzeChoice(choice, new CheeseGrid(grid), team))
                .collect(Collectors.toList());
    }

    private SimulationNode analyzeChoice(ColumnShiftCommand shift, CheeseGrid copyGrid, Team team) {
        SimulationNode node = new SimulationNode(shift, copyGrid, team, null);
        Analysis.analyzeShift(node);
        return node;
    }
}
