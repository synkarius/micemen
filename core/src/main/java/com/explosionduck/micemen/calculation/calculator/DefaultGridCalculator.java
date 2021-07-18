package com.explosionduck.micemen.calculation.calculator;

import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.calculation.FilteringBlockIterator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.command.grid.MouseEscapeCommand;
import com.explosionduck.micemen.command.grid.MouseMoveCommand;
import com.explosionduck.micemen.dagger.interfaces.Stateless;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.Direction;
import com.explosionduck.micemen.domain.Point;
import com.explosionduck.micemen.domain.blocks.Block;
import com.explosionduck.micemen.domain.blocks.Mouse;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.util.BoardCalcUtils;

import java.util.*;

import static java.util.Collections.singleton;

@Stateless
public class DefaultGridCalculator implements GridCalculator {

    private final BoardScorer boardScorer;

    public DefaultGridCalculator(BoardScorer boardScorer) {
        this.boardScorer = boardScorer;
    }

    @Override
    public Collection<Command> calculateNewCommands(CheeseGrid grid) throws CheeseException {
        CheeseGrid copygrid = new CheeseGrid(grid);
        Team lastTeam = grid.getActiveTeam() == Team.RED ? Team.RED : Team.BLUE;

        return this.recalculate(copygrid, lastTeam);
    }

    @Override
    public ColumnShiftCommand[] getChoices(CheeseGrid grid) {
        ColumnShiftCommand[] choices = new ColumnShiftCommand[grid.getWidth() * 2];

        for (int p = 0; p < grid.getPoles().length; p++) {
            if (grid.getPoles()[p] && BoardCalcUtils.poleIsAvailable(grid, p)) {
                choices[p] = new ColumnShiftCommand(this, p, Direction.UP);
                choices[grid.getWidth() + p] = new ColumnShiftCommand(this, p, Direction.DOWN);
            }
        }

        Comparator<ColumnShiftCommand> comparator = grid.getActiveTeam() == Team.RED
                ? DefaultGridCalculator::redSort
                : DefaultGridCalculator::blueSort;
        Arrays.sort(choices, comparator);

        return choices;
    }

    private int walkingDir(Team team) {
        return team == Team.RED ? 1 : -1;
    }

    private boolean mustFall(CheeseGrid grid, int x, int y) {
        if (y + 1 > grid.getHeightMax())
            return false;
        return grid.getBlockAt(x, y + 1).isEmpty();
    }

    private boolean canMove(CheeseGrid grid, int x, int y, Team team) {
        int dir = walkingDir(team);
        int newX = x + dir;
        if (newX < 0 || newX > grid.getWidthMax())
            return false;
        return grid.getBlockAt(newX, y).isEmpty();
    }

    private List<Command> recalculate(CheeseGrid copygrid, Team lastTeam) throws CheeseException {
        final List<Command> allNewCommands = new ArrayList<>();
        final boolean red = lastTeam == Team.BLUE;
        final Direction activeDir = red ? Direction.LEFT : Direction.RIGHT, //
                inactiveDir = !red ? Direction.LEFT : Direction.RIGHT;
        final int activeStart = red ? copygrid.getWidthMax() : 0, //
                inActiveStart = !red ? copygrid.getWidthMax() : 0;
        final Team otherTeam = lastTeam == Team.RED ? Team.BLUE : Team.RED;

        while (true) {

            /* first, look for falls */
            Iterator<Mouse> allMice = new FilteringBlockIterator<>(copygrid, Direction.UP, activeDir, activeStart, copygrid.getHeightMax(),
                    Mouse.class);
            var commands = this.findFirstMove(copygrid, allMice, true);
            if (commands != null && allNewCommands.addAll(commands))
                continue;

            /* then, the active team moves */
            Iterator<Mouse> activeTeam = new FilteringBlockIterator<>(copygrid, Direction.UP, activeDir, activeStart,
                    copygrid.getHeightMax(), Mouse.class).team(lastTeam);
            commands = this.findFirstMove(copygrid, activeTeam, false);
            if (commands != null && allNewCommands.addAll(commands))
                continue;

            /* non-active team moves last */
            Iterator<Mouse> nonactiveTeam = new FilteringBlockIterator<>(copygrid, Direction.UP, inactiveDir, inActiveStart,
                    copygrid.getHeightMax(), Mouse.class).team(otherTeam);
            commands = this.findFirstMove(copygrid, nonactiveTeam, false);
            if (commands != null && allNewCommands.addAll(commands))
                continue;

            /* if no moves found, stop */
            break;
        }

        return allNewCommands;
    }

    private Collection<Command> findFirstMove(CheeseGrid copygrid, Iterator<Mouse> miceIter, boolean fallsOnly)
            throws CheeseException {
        // TODO: mutating the copygrid as part of the move-getting process is
        //  kind of sloppy
        //  -- better to create full MouseMoves and MouseEscapeCommands and apply
        //  them on the spot

        while (miceIter.hasNext()) {
            Mouse mouse = miceIter.next();
            MouseMoveCommand move = getMoves(mouse, copygrid, fallsOnly);
            Point total = move.consolidate();
            int totalX = Math.abs(total.x);
            int totalY = Math.abs(total.y);

            if (fallsOnly) {
                if (totalX == 0 && totalY > 0) {
                    return singleton(move);
                }
            } else {
                if (totalX + totalY > 0) {
                    MouseEscapeCommand mouseEscapeCommand = this.removeEscapedMice(copygrid);
                    if (mouseEscapeCommand != null) {
                        Collection<Command> commands = new ArrayList<>();
                        commands.add(move);
                        commands.add(mouseEscapeCommand);

                        /* must also immediately update copygrid */
                        mouseEscapeCommand.executeOnCopygrid(copygrid);

                        return commands;
                    }
                    return singleton(move);
                }
            }
        }
        return null;
    }

    private MouseEscapeCommand removeEscapedMice(CheeseGrid grid) {
        Mouse mouse;
        Block left = grid.getBlockAt(0, grid.getHeightMax());
        Block right = grid.getBlockAt(grid.getWidthMax(), grid.getHeightMax());
        if (left.isMouse()) {
            mouse = (Mouse) left;
        } else if (right.isMouse()) {
            mouse = (Mouse) right;
        } else {
            return null;
        }

        return new MouseEscapeCommand(mouse, this.boardScorer);
    }

    private MouseMoveCommand getMoves(Mouse mouse, CheeseGrid grid, boolean fallsOnly) throws CheeseException {
        Point origin = grid.getBlockCoordinates(mouse);
        int x = origin.x;
        int y = origin.y;

        MouseMoveCommand result = new MouseMoveCommand(x, y);
        while (true) {
            int ox = x;
            int oy = y;

            if (mustFall(grid, x, y)) {
                y += 1;
                result.add(0, 1);
            } else if (canMove(grid, x, y, mouse.getTeam()) && !fallsOnly) {
                int dir = walkingDir(mouse.getTeam());
                x += dir;
                result.add(dir, 0);
            } else {
                break;
            }

            if (!grid.coordinatesAreValid(x, y))
                throw new CheeseException("Bad move detected.");

            grid.switchBlocks(ox, oy, x, y);
        }

        return result;
    }

    private static int redSort(ColumnShiftCommand a, ColumnShiftCommand b) {
        if (a == null && b == null)
            return 0;
        if (a == null)
            return 1;
        if (b == null)
            return -1;

        int xCompare = Integer.compare(a.getX(), b.getX());
        if (xCompare != 0)
            return xCompare;
        return a.getDirection().compareTo(b.getDirection());
    }

    private static int blueSort(ColumnShiftCommand a, ColumnShiftCommand b) {
        if (a == null && b == null)
            return 0;
        if (a == null)
            return 1;
        if (b == null)
            return -1;

        int xCompare = Integer.compare(b.getX(), a.getX());
        if (xCompare != 0)
            return xCompare;
        return a.getDirection().compareTo(b.getDirection());
    }
}
