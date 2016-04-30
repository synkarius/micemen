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
    
    /** time before dropping */
    private static final int FACE_TIME = 60;
    /** duration of dropping */
    private static final int DROP_TIME = 180;
    
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
            flashy = score == grid.micePerTeam();
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
    
    private void exit(CheeseGrid grid) {
        int drop = flashy ? DROP_TIME * 3 : DROP_TIME;
        int face = flashy ? FACE_TIME : FACE_TIME;
        if (++counter < drop) {
            if (flashy)
                grid.state().anim().muscle(mouse);
            else
                mouse.graphic(Graphic.FACE_CAMERA);
        } else {
            mouse.graphic(Graphic.FALL);
            // TODO: make Scenegraph drop the mouse first 13 pixels at once,
            // then 2 per frame for 36 frames
            
            if (grid.isGraphical())
                Resource.jump.play();
            
            if (counter >= face)
                vanishFinish(grid);
        }
    }
    
    private void vanishFinish(CheeseGrid grid) {
        if (grid.isGraphical()) {
            Scores scores = grid.ctrl().scores();
            grid.state().menu().redScore = scores.red;
            grid.state().menu().blueScore = scores.blue;
        }
        
        grid.eliminate(mouse);
        finished = true;
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
