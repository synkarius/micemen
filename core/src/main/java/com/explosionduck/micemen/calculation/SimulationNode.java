package com.explosionduck.micemen.calculation;

import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.Direction;
import com.explosionduck.micemen.domain.blocks.Team;

public class SimulationNode {

    private final ColumnShiftCommand columnShiftCommand;
    private final CheeseGrid copyGrid;
    private final Team team;
    private final ColumnShiftCommand rootColumnShiftCommand;
    private final int x;
    private final Direction direction;
    private int value;

    public SimulationNode(
            ColumnShiftCommand columnShiftCommand,
            CheeseGrid copyGrid,
            Team team,
            ColumnShiftCommand rootColumnShiftCommand) {
        this.columnShiftCommand = columnShiftCommand;
        this.copyGrid = copyGrid;
        this.team = team;
        this.rootColumnShiftCommand = rootColumnShiftCommand;
        this.x = columnShiftCommand.getX();
        this.direction = columnShiftCommand.getDirection();
    }

    public ColumnShiftCommand getColumnShiftCommand() {
        return columnShiftCommand;
    }

    public CheeseGrid getCopyGrid() {
        return copyGrid;
    }

    public Team getTeam() {
        return team;
    }

    public ColumnShiftCommand getRootColumnShiftCommand() {
        return rootColumnShiftCommand;
    }

    public int getX() {
        return x;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getValue() {
        return value;
    }

    public void setValue(BoardScorer.Scores scores) {
        this.value = this.team == Team.RED
                ? scores.redBoardValue
                : scores.blueBoardValue;
    }
}
