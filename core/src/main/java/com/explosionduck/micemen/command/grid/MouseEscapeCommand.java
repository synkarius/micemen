package com.explosionduck.micemen.command.grid;

import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.command.menu.SetGameOverCommand;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Mouse;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.fx.MouseGraphic;
import com.explosionduck.micemen.fx.Sounds;

import java.util.Collection;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public class MouseEscapeCommand implements Command {

    /**
     * time before dropping
     */
    private static final int FACE_TIME = 60;
    /**
     * duration of dropping
     */
    private static final int DROP_TIME = 105;

    private final BoardScorer scorer;

    private Mouse mouse;
    private boolean finished;
    private boolean setup;
    private boolean isFinalMouse;
    private boolean soundPlayed;
    private int ticker;

    public MouseEscapeCommand(Mouse mouse, BoardScorer scorer) {
        this.mouse = mouse;
        this.scorer = scorer;
    }

    @Override
    public Collection<Command> execute(GameContext gameContext) {
        var grid = gameContext.getGrid();
        if (!grid.isGraphical()) {
            this.conclude(gameContext);
            return emptySet();
        }

        if (!this.setup) {
            var team = mouse.getTeam();
            int x = team == Team.RED ? grid.getWidthMax() : 0;
            int y = grid.getHeightMax();
            this.mouse = (Mouse) grid.getBlockAt(x, y);
            int score = scorer.score(grid, team);
            this.isFinalMouse = score == grid.getMicePerTeam() - 1;
            this.mouse.setGraphic(!isFinalMouse ? MouseGraphic.FACE_CAMERA : MouseGraphic.MUSCLE1);
            this.setup = true;
        }

        this.tick(gameContext);

        return this.finished && this.isFinalMouse
                ? singleton(new SetGameOverCommand(this.mouse.getTeam()))
                : emptySet();
    }

    @Override
    public boolean isComplete() {
        return this.finished;
    }

    @Override
    public CommandType getType() {
        return CommandType.MUSCLE_FLEX_DROP;
    }

    private void tick(GameContext context) throws CheeseException {
        var grid = context.getGrid();
        if (!grid.isGraphical()) {
            throw new CheeseException("Non-graphical grid needn't do exit animations.");
        }

        if (this.isFinalMouse) {
            if (this.mouse.getGraphic() == MouseGraphic.FALL) {
                if (!this.soundPlayed) {
                    Sounds.JUMP.play();
                    this.soundPlayed = true;
                }

                if (++ticker >= DROP_TIME) {
                    this.conclude(context);
                }
            }
        } else {
            if (++this.ticker >= FACE_TIME) {
                if (!this.soundPlayed) {
                    Sounds.JUMP.play();
                    this.soundPlayed = true;
                    this.mouse.setGraphic(MouseGraphic.FALL);
                    this.ticker = 0;
                }
                if (this.ticker >= DROP_TIME) {
                    this.conclude(context);
                }
            }
        }
    }

    private void conclude(GameContext context) {
        var grid = context.getGrid();

        grid.deleteMouse(this.mouse);
        this.finished = true;

        if (grid.isGraphical()) {
            var scores = this.scorer.scores(grid, false);
            context.setRedScore(scores.redScore);
            context.setBlueScore(scores.blueScore);
        }
    }

    /**
     * when it is created (by a copygrid), the copygrid needs to have its state
     * changed -- this method executes the removal on the copygrid and then
     * resets the order for use by the real grid
     */
    public void executeOnCopygrid(CheeseGrid copygrid) {
        copygrid.deleteMouse(this.mouse);
        this.ticker = 0;
        this.finished = false;
    }

}
