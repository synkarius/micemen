package com.explosionduck.micemen.command.grid;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.Point;
import com.explosionduck.micemen.domain.blocks.Mouse;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.fx.MouseGraphic;
import com.explosionduck.micemen.fx.Sounds;

import java.util.*;

public class MouseMoveCommand implements Command {

    private static final int DELAY = 20;
    private final List<Point> moves;
    private final int ox;
    private final int oy;
    private boolean finished;
    private Mouse applicableMouse;
    private Iterator<Point> iterator;
    private int counter;

    public MouseMoveCommand(int ox, int oy) {
        this.ox = ox;
        this.oy = oy;
        this.moves = new ArrayList<>();
    }

    public void add(int xDif, int yDif) {
        moves.add(new Point(xDif, yDif));
    }

    @Override
    public Collection<Command> execute(GameContext gameContext) {
        CheeseGrid grid = gameContext.getGrid();
        if (iterator == null) {
            iterator = moves.iterator();
        }

        if (applicableMouse == null) {
            applicableMouse = (Mouse) grid.getBlockAt(ox, oy);
            gameContext.getAnimationFrameMap().reset(applicableMouse);
        }

        if (grid.isGraphical()) {
            if (counter++ > DELAY) {
                counter = 0;
            } else { // delay:
                return Collections.emptySet();
            }
        }

        if (iterator.hasNext()) {
            Point current = grid.getBlockCoordinates(applicableMouse);
            /* "next" is really a vector, not a point */
            Point next = iterator.next();
            Point destination = current.add(next);

            if (grid.isGraphical()) {
                if (next.x != 0) {
                    Sounds.LO_2.play();
                } else if (next.y != 0) {
                    Sounds.LO.play();
                }

                if (destination.x == 0 || destination.x == grid.getWidthMax()) {
                    applicableMouse.setGraphic(MouseGraphic.UMBRELLA);
                } else {
                    gameContext.getAnimationFrameMap().walk(applicableMouse);
                }
            }

            grid.switchBlocks(current, destination);
        }

        finished = !iterator.hasNext();

        if (finished) {
            gameContext.getAnimationFrameMap().reset(applicableMouse);
        }

        return Collections.emptySet();
    }

    @Override
    public boolean isComplete() {
        return finished;
    }

    public Point consolidate() {
        return moves.stream().reduce(new Point(0, 0), Point::add);
    }

    @Override
    public CommandType getType() {
        return CommandType.MOUSE_MOVE;
    }
}
