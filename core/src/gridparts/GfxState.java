package gridparts;

import java.util.HashMap;
import java.util.Map;

import graphical.GridGfx.Graphic;
import model.Block;
import model.CheeseGrid;
import model.Mouse;
import model.Mouse.Team;

/** for tracking changes to graphical state which are not a part of the model */
public class GfxState {
    
    private CheeseGrid          grid;
    
    public Integer              columnShifting;
    public int                  yOffset;
    
    private Map<Block, Integer> animFrame = new HashMap<>();
    
    private Menu                menu;
    
    public GfxState(CheeseGrid grid) {
        this.grid = grid;
        this.menu = new Menu();
    }
    
    public GfxState init() {
        for (int x = 0; x < grid.width(); x++)
            for (int y = 0; y < grid.height(); y++)
                if (grid.get(x, y).isMouse())
                    animFrame.put(grid.get(x, y), 0);
        return this;
    }
    
    public void putFrame(Mouse mouse, Integer frame) {
        if (animFrame.containsKey(mouse)) {
            animFrame.put(mouse, frame);
        }
    }
    
    public void reset(Mouse mouse) {
        mouse.graphic(Graphic.STAND);
        putFrame(mouse, 0);
    }
    
    public Integer getFrame(Mouse mouse) {
        return animFrame.get(mouse);
    }
    
    public Menu menu() {
        return menu;
    }
    
    public static class Menu {
        public int    redScore;
        public int    blueScore;
        public String text = "T";
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
