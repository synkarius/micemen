package control;

import model.CheeseGrid;
import model.Mouse.Team;
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
    
    public KeyboardController setControllers(IController red, IController blue) {
        if (!controllersAreSetUp()) {
            this.red = red;
            this.blue = blue;
        }
        return this;
    }
    
    public KeyboardController setOpponent(boolean config) {
        if (config) {
            
        } else {
            
        }
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
            String teamName = Team.RED.toString();
            if (Math.random() > .5) {
                teamName = Team.BLUE.toString();
                grid.ctrl().orders().add(0, new PassTurn());
            }
            // TODO: grid.menu.message(teamName + " first")
        } else {
            grid.ctrl().orders().add(0, new PassTurn());
        }
    }
    
    private boolean controllersAreSetUp() {
        return red != null && blue != null;
    }
    
    private boolean isMenuConfirm() {
        return mode == ControlMode.CONFIRM_NEW_GAME || mode == ControlMode.CONFIRM_SAVE
                || mode == ControlMode.CONFIRM_LOAD || mode == ControlMode.CONFIRM_QUIT;
    }
    
    
    @Override
    public IOrder getOrder() {
        return null;
    }
    
}
