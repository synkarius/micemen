package graphical;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import graphical.Resource.Graphic;
import model.Mouse.Team;
import model.SimPoint;

public class TitleScreen {
    private static final List<SimPoint> cheese = new ArrayList<>();
    private static final int            ACROSS = 21;
    private static final int            DOWN   = 13;
    static {
        for (int x = 0; x < ACROSS; x++) {
            cheese.add(new SimPoint(x, 0));
            cheese.add(new SimPoint(x, DOWN - 1));
        }
        for (int y = 1; y < DOWN - 1; y++) {
            cheese.add(new SimPoint(0, y));
            cheese.add(new SimPoint(ACROSS - 1, y));
        }
    }
    
    private static final List<TitleMouse> mice  = new ArrayList<>();
    private static final int              SPEED = 25;
    static {
        for (int i = 0; i < 6; i++) {
            for (Team team : Team.values()) {
                int next = 0;
                int randomY = (int) Math.round(DOWN + Math.random() * 5);
                int x = team == Team.RED ? i + 1 : 20 - i - 1;
                
                TitleMouse controller = new TitleMouse(x, randomY, team, Graphic.STAND);
                controller.changes.put(next++ * SPEED, Change.UMBRELLA);
                for (int n = 0; n < 17; n++)
                    controller.changes.put(next++ * SPEED, Change.DOWN);
                controller.changes.put(next++ * SPEED, Change.FACE_CAMERA);
                mice.add(controller);
            }
        }
    }
    
    private static int       frame;
    private static boolean   finished;
    private static final int DRAW_TITLE = 150;
    private static final int CREDITS_1  = DRAW_TITLE + 50;
    private static final int CREDITS_2  = CREDITS_1 + 50;
    
    public static void draw(SpriteBatch batch) {
        boolean skip = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER);
        if (skip) {
            finished = true;
            return;
        }
        
        for (SimPoint c : cheese)
            batch.draw(Resource.cheese, c.x() * SceneGraph.BLOCK_SIZE + SceneGraph.X_OFFSET,
                    c.y() * SceneGraph.BLOCK_SIZE + SceneGraph.Y_OFFSET);
        
        for (TitleMouse mouse : mice) {
            mouse.play(frame);
            Sprite sprite = new Sprite(mouse.team == Team.RED ? mouse.graphic.red() : mouse.graphic.blue());
            sprite.setAlpha(mouse.alpha);
            sprite.setPosition(mouse.x * SceneGraph.BLOCK_SIZE + SceneGraph.X_OFFSET,
                    mouse.y * SceneGraph.BLOCK_SIZE + SceneGraph.Y_OFFSET);
            sprite.draw(batch);
        }
        
        if (frame > DRAW_TITLE)
            Resource.large.draw(batch, "Mice Men: Remix", 220, 300);
        if (frame > CREDITS_1)
            Resource.normal.draw(batch, "by synkarius", 275, 250);
        if (frame > CREDITS_2)
            Resource.normal.draw(batch, "original by William Soleau", 235, 220);
        
        finished = frame++ == 25 * SPEED;
    }
    
    public static boolean isFinished() {
        if (finished) {
            cheese.clear();
            mice.clear();
        }
        return finished;
    }
    
    private static class TitleMouse {
        Team                 team;
        int                  x;
        int                  y;
        Graphic              graphic;
        float                alpha;
        Map<Integer, Change> changes = new HashMap<>();
        
        TitleMouse(int x, int y, Team team, Graphic graphic) {
            this.x = x;
            this.y = y;
            this.team = team;
            this.graphic = graphic;
        }
        
        void play(int time) {
            Change action = changes.get(time);
            if (action == null)
                return;
            
            switch (action) {
                case DOWN:
                    move(-1, 0);
                    break;
                case UP:
                    move(1, 0);
                    break;
                case LEFT:
                    move(-1, 0);
                    break;
                case RIGHT:
                    move(1, 0);
                    break;
                case FALL:
                    graphic = Graphic.FALL;
                    break;
                case UMBRELLA:
                    graphic = Graphic.UMBRELLA;
                    break;
                case FACE_CAMERA:
                    graphic = Graphic.FACE_CAMERA;
                    break;
                case WALK:
                    graphic = Graphic.WALK;
                    break;
                case VISIBLE:
                    alpha = 1;
                    break;
            }
        }
        
        void move(int updown, int leftright) {
            y += updown;
            if (y == DOWN - 2)
                alpha = (float) .5;
            else if (y == DOWN - 4)
                alpha = 1;
            if (y < 1) {
                y = 1;
                graphic = Graphic.STAND;
            }
            
            x += leftright;
        }
    }
    
    private enum Change {
        UP, DOWN, LEFT, RIGHT,
        
        FALL, FACE_CAMERA, WALK, UMBRELLA,
        
        VISIBLE;
    }
}
