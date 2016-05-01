package gridparts;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import graphical.Resource.Graphic;
import model.Block;
import model.CheeseGrid;
import model.Mouse;
import model.Mouse.Team;

/** for tracking changes to graphical state which are not a part of the model */
public class GfxState {
    
    private CheeseGrid         grid;
    
    public Integer             columnShifting;
    public int                 yOffset;
    public float               redHandYOffset;
    public float               blueHandYOffset;
    
    private Menu               menu;
    private Anim               anim;
    
    private static final float FALL_OFFSET           = -12;
    private static final float FALL_FRAME_MULTIPLIER = (float) 0.25;
    
    public GfxState(CheeseGrid grid) {
        this.grid = grid;
        this.menu = new Menu();
        this.anim = new Anim();
    }
    
    public void randomMouseEatsCheese() {
        if (Math.random() > .9960) {
            List<Mouse> mice = grid.ctrl()
                    .getAllMice()
                    .stream()
                    .filter(mouse -> mouse.graphic() == Graphic.POINT)
                    .collect(Collectors.toList());
            if (mice.size() > 0) {
                Collections.shuffle(mice);
                mice.get(0).graphic(Graphic.EAT1);
            }
        }
    }
    
    public GfxState init() {
        // for (int x = 0; x < grid.width(); x++)
        // for (int y = 0; y < grid.height(); y++)
        // if (grid.get(x, y).isMouse())
        // anim.animFrame.put(grid.get(x, y), 0);
        return this;
    }
    
    public Anim anim() {
        return anim;
    }
    
    public static class Anim {
        private Map<Block, Integer> frameMap = new HashMap<>();
        
        public void reset(Mouse mouse) {
            mouse.graphic(Graphic.STAND);
            frameMap.put(mouse, 0);
        }
        
        /** alternates between walking and standing graphics */
        public void walk(Mouse mouse) {
            int frame = frameMap.getOrDefault(mouse, 0);
            ;
            int next = frame == 0 ? 1 : 0;
            frameMap.put(mouse, next);
            
            if (next == 1)
                mouse.graphic(Graphic.WALK);
            else
                mouse.graphic(Graphic.STAND);
        }
        
        public float mouseFallOffset(Mouse mouse) {
            int frame = frameMap.getOrDefault(mouse, 0);
            frameMap.put(mouse, --frame);
            return FALL_OFFSET + frame * FALL_FRAME_MULTIPLIER;
        }
        
        public Graphic getFrameForMuscle(Mouse mouse) {
            int frame = frameMap.getOrDefault(mouse, 0);
            int frameScaled = frame / 25;
            Graphic graphic;
            
            if (frameScaled >= Graphic.ANIM_FLEX.size()) {
                frame = 0;
                graphic = Graphic.FALL; // signals fall
                mouse.graphic(graphic);
            } else {
                graphic = Graphic.ANIM_FLEX.get(frameScaled);
            }
            
            frameMap.put(mouse, ++frame);
            return graphic;
        }
        
        public Graphic getFrameForEatingMouse(Mouse mouse) {
            // boolean finished = false;
            int frame = frameMap.getOrDefault(mouse, 0);
            int frameScaled = frame / 10;
            Graphic graphic;
            
            if (frameScaled >= Graphic.ANIM_EAT.size()) {
                frame = 0;
                graphic = Graphic.POINT;
                mouse.graphic(graphic);
            } else {
                graphic = Graphic.ANIM_EAT.get(frameScaled);
            }
            
            frameMap.put(mouse, ++frame);
            return graphic;
        }
    }
    
    public Menu menu() {
        return menu;
    }
    
    public static class Menu {
        public int    redScore;
        public int    blueScore;
        public String text    = "T";
        public String message = "M";
        
        public void boardFavor(Team team, int difference) {
            message = "[" + team.toString() + " : " + difference + "]";
        }
        
        public void gameOverWithWinner(Team team) {
            text = team.toString() + " wins! Play again? [Y/N]";
        }
        
        public void menu() {
            text = "<N>ew   <L>oad   <S>ave   <Q>uit";
        }
        
        public void thinking() {
            message = "Thinking ...";
        }
        
        public void pressAnyKey() {
            text = "Press Any Key to Decide Who Goes First";
        }
        
        public void confirmQuit() {
            text = "Really Quit? [Y/N]";
        }
        
        public void confirmNewGame() {
            text = "Really Start New Game? [Y/N]";
        }
        
        public void confirmSave() {
            text = "Really Save? [Y/N]";
        }
        
        public void confirmLoad() {
            text = "Really Load? [Y/N]";
        }
        
        public void chooseDifficulty() {
            text = "Difficulty:   <H>ard    <M>edium    <E>asy";
        }
        
        public void chooseOpponent() {
            text = "Play Against:   <H>uman   <C>omputer";
        }
        
    }
}
