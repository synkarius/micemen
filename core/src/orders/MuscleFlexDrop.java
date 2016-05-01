package orders;

import graphical.Resource;
import graphical.Resource.Graphic;
import gridparts.GridController.Scores;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse;
import model.Mouse.Team;

public class MuscleFlexDrop implements IOrder {
    
    private Mouse            mouse;
    private int              counter;
    private boolean          finished;
    private boolean          setup;
    private boolean          flashy;
    private boolean          soundPlayed;
    
    /** time before dropping */
    private static final int FACE_TIME = 60;
    /** duration of dropping */
    private static final int DROP_TIME = 105;
    
    private void exit(CheeseGrid grid) throws CheeseException {
        if (!grid.isGraphical())
            throw new CheeseException("Non-graphical grid needn't do exit animations.");
        
        if (!flashy) {
            if (++counter >= FACE_TIME) {
                if (!soundPlayed) {
                    Resource.jump.play();
                    soundPlayed = true;
                    mouse.graphic(Graphic.FALL);
                    counter = 0;
                }
                if (counter >= DROP_TIME)
                    vanishFinish(grid);
            }
        } else {
            if (mouse.graphic() == Graphic.FALL) {
                if (!soundPlayed) {
                    Resource.jump.play();
                    soundPlayed = true;
                }
                
                if (++counter >= DROP_TIME)
                    vanishFinish(grid);
            }
        }
    }
    
    public MuscleFlexDrop(Mouse mouse, int x) {
        this.mouse = mouse;
    }
    
    @Override
    public void execute(CheeseGrid grid) throws CheeseException {
        if (!grid.isGraphical())
            vanishFinish(grid);
        
        if (!setup) {
            Team team = mouse.team();
            mouse = mouse.getOriginal(grid.id());
            int score = grid.ctrl().score(team);
            flashy = score == grid.micePerTeam() - 1;
            mouse.graphic(!flashy ? Graphic.FACE_CAMERA : Graphic.MUSCLE1);
            setup = true;
        }
        
        exit(grid);
    }
    
    @Override
    public boolean finished() {
        return finished;
    }
    
    @Override
    public OrderType type() {
        return OrderType.MUSCLE_FLEX_DROP;
    }
    
    private void vanishFinish(CheeseGrid grid) {
        grid.eliminate(mouse);
        finished = true;
        
        if (grid.isGraphical()) {
            Scores scores = grid.ctrl().scores();
            grid.state().menu().redScore = scores.red;
            grid.state().menu().blueScore = scores.blue;
        }
    }
    
    /**
     * when it is created (by a copygrid), the copygrid needs to have its state
     * changed -- this method executes the removal on the copygrid and then
     * resets the order for use by the real grid
     */
    public void executeOnCopygrid(CheeseGrid copygrid) {
        vanishFinish(copygrid);
        counter = 0;
        finished = false;
    }
    
}
