package com.explosionduck.micemen.command.grid;

import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.Direction;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.fx.Sounds;
import com.explosionduck.micemen.screens.game.GameScreen;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.emptySet;

public class ColumnShiftCommand implements Command {

    private static final int PX_INCREMENT = 1;

    private final GridCalculator calculator;
    private final int endTick;
    private final int x;
    private final Direction direction;

    private int tick;

    public ColumnShiftCommand(GridCalculator calculator, int x, Direction direction) {
        this.calculator = calculator;
        this.x = x;
        this.direction = direction;
        this.endTick = GameScreen.BLOCK_SIZE + 1;
    }

    @Override
    public Collection<Command> execute(GameContext gameContext) {
        var grid = gameContext.getGrid();
        /** update y offsets for graphical shift */
        int d = direction == Direction.UP ? PX_INCREMENT : -PX_INCREMENT;
        gameContext.setColumnShifting(x);
        gameContext.setYOffset(gameContext.getYOffset() + d);

        if (grid.isGraphical() && tick == 0) {
            Sounds.HI.play();
        }

        tick += PX_INCREMENT;

        /** don't wait if it's not the on-screen grid */
        if (!grid.isGraphical()) {
            tick = endTick;
        }

        /* when done, update model */
        if (isComplete()) {
            grid.shiftColumn(direction, x);
            grid.setActivePole(x);

            var newCommands = new ArrayList<>(calculator.calculateNewCommands(grid));
            newCommands.add(new PassTurnCommand());

            gameContext.setColumnShifting(null);
            gameContext.setYOffset(0);

            return newCommands;
        }

        return emptySet();
    }

    @Override
    public boolean isComplete() {
        return tick == endTick;
    }

    @Override
    public CommandType getType() {
        return CommandType.COLUMN_SHIFT;
    }

    public int getX() {
        return x;
    }

    public Direction getDirection() {
        return direction;
    }
}
