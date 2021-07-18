package com.explosionduck.micemen.command.grid;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Block;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.fx.MouseGraphic;
import com.explosionduck.micemen.util.BoardCalcUtils;

import java.util.Collection;
import java.util.function.Consumer;

import static java.util.Collections.emptySet;

public class PassTurnCommand implements Command {

    @Override
    public Collection<Command> execute(GameContext gameContext) {
        CheeseGrid grid = gameContext.getGrid();
        grid.setVerbotenColumn(null);

        boolean doBlue = grid.getActiveTeam() == Team.RED;

        /* set poles and get leftmost */
        Team nextTeam = doBlue ? Team.BLUE : Team.RED;
        Integer leftmost = null;
        int count = 0;
        int only = -10;
        for (int x = 0; x < grid.getWidth(); x++) {
            boolean hasMice = BoardCalcUtils.columnMouseCount(grid, x, nextTeam) > 0;

            grid.getPoles()[x] = hasMice;

            if (hasMice) {
                count++;
                only = x;
            }

            if (x == grid.getActivePole()) {
                if (hasMice) // only activate the "X" when the pole is visible
                    grid.setVerbotenColumn(x);
                continue; // not eligible position for "leftmost"
            }

            if (leftmost == null && hasMice)
                leftmost = x;
        }

        // set hand on screen
        Consumer<Integer> currentHand = doBlue ? grid::setRedHand : grid::setBlueHand;
        Consumer<Integer> nextHand = doBlue ? grid::setBlueHand : grid::setRedHand;
        // deactivate current hand
        currentHand.accept(null);
        grid.setActiveTeam(nextTeam);

        // update animations
        if (grid.isGraphical()) {
            changeTeamAnimations(grid, nextTeam);
        }

        // section to handle if there's only 1 pole:
        if (count < 2) {
            grid.setVerbotenColumn(null);
            leftmost = only;
        }

        // activate the next hand
        grid.setActivePole(leftmost);
        nextHand.accept(leftmost);

        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.PASS_TURN;
    }

    private void changeTeamAnimations(CheeseGrid grid, Team active) {
        for (int x = 1; x < grid.getWidth() - 1; x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Block block = grid.getBlockAt(x, y);
                if (block.isMouse()) {
                    boolean shouldPoint = block.isRedMouse() && Team.RED == active;
                    shouldPoint |= block.isBlueMouse() && Team.BLUE == active;

                    block.setGraphic(shouldPoint ? MouseGraphic.POINT : MouseGraphic.STAND);
                }
            }
        }
    }

}
