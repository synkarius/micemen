package control;

import entity.sim.Mouse.Team;
import model.CheeseGrid;
import orders.IOrder;
import orders.PassTurn;

public class KeyboardController implements IController {
    
    enum ControlMode {
        CHOOSE_OPPONENT, CHOOSE_DIFFICULTY,
        
        CONFIRM_NEW_GAME, CONFIRM_SAVE, CONFIRM_LOAD, CONFIRM_QUIT,
        
        GAME, GAME_OVER
    }
    
    private IController red;
    private IController blue;
    private CheeseGrid  grid;
    private ControlMode mode;
    
    public KeyboardController setState() {
        
        return this;
    }
    
    public KeyboardController setGrid(CheeseGrid grid) {
        this.grid = grid;
        return this;
    }
    
    public KeyboardController setMode(ControlMode mode) {
        this.mode = mode;
        return this;
    }
    
    public boolean isReady() {
        return mode != null && grid != null; // TODO?: && state != null
    }
    
    private boolean isNewGameChoices() {
        return mode == ControlMode.CHOOSE_OPPONENT || mode == ControlMode.CHOOSE_DIFFICULTY;
    }
    
    public void startGame(Team team) {
        // grid.menu.menu(); // TODO
        mode = ControlMode.GAME;
        grid.ctrl().orders().add(0, new PassTurn());
        
        if (team == null) {
            
        } else {
            // grid.ctrl().orders().add(0, )
        }
    }
    
    @Override
    public IOrder getOrder() {
        return null;
    }
    
}
