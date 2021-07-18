package com.explosionduck.micemen.command.grid;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.domain.Direction;
import com.explosionduck.micemen.util.BoardCalcUtils;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.command.CommandType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class SetHandCommand implements Command {

    private final Team team;
    private final int x;
    private final Consumer<Integer> handSet;
    private final Supplier<Integer> handGet;
    private final boolean isRelative;

    public SetHandCommand(CheeseGrid grid, int x, boolean isRelative) {
        this.team = grid.getActiveTeam();
        this.x = x;
        this.handSet = this.team == Team.RED ? grid::setRedHand : grid::setBlueHand;
        this.handGet = this.team == Team.RED ? grid::getRedHand : grid::getBlueHand;
        this.isRelative = isRelative;
    }

    public SetHandCommand(CheeseGrid grid, int x) {
        this(grid, x, false);
    }

    @Override
    public Collection<Command> execute(GameContext gameContext) {
        CheeseGrid grid = gameContext.getGrid();
        if (!isRelative) {
            handSet.accept(x);
            return Collections.emptySet();
        }

        Direction direction = x < 0 ? Direction.LEFT : Direction.RIGHT;

        List<Integer> gridIndices = new ArrayList<>();
        for (int i = 2; i < grid.getWidth() - 2; i++) { // first and last two columns should never be available
            gridIndices.add(i);
        }
        if (direction == Direction.LEFT) {
            Collections.reverse(gridIndices);
        }

        // get available next pole in relativeX direction
        int available = 0;
        for (Integer x : gridIndices) {
            if (x.equals(grid.getVerbotenColumn())) {
                continue;
            }

            if (BoardCalcUtils.poleIsAvailable(grid, x)) {
                boolean right = direction == Direction.RIGHT && x > handGet.get();
                boolean left = direction == Direction.LEFT && x < handGet.get();
                if (right || left) {
                    available = x;
                    break;
                }
            }
        }
        if (available != 0) {
            grid.setActivePole(available);
            handSet.accept(available);
        }

        return Collections.emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.SET_HAND;
    }
}