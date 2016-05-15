package control;

import java.io.IOException;
import java.util.concurrent.ExecutorService;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import data.DataAccessor;
import file.Board;
import gridparts.GridController;
import gridparts.GridController.Scores;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import orders.IOrder;
import orders.OrderType;
import orders.PassTurn;
import orders.SetHand;
import simulate.Simulator;
import util.Restart;

public class KeyboardController implements IController {
    
    public enum ControlMode {
        CHOOSE_OPPONENT, CHOOSE_DIFFICULTY,
        
        CONFIRM_NEW_GAME, CONFIRM_SAVE, CONFIRM_LOAD, CONFIRM_QUIT,
        
        GAME, GAME_OVER
    }
    
    private IController     red;
    private IController     blue;
    private Boolean         choseCPUOpponent;
    private CheeseGrid      grid;
    private ControlMode     mode;
    private Restart         restart;
    
    private ExecutorService pool;
    private DataAccessor    dao;
    
    public KeyboardController(ExecutorService pool, DataAccessor dao) {
        this.pool = pool;
        this.dao = dao;
    }
    
    public KeyboardController setRestart(Restart restart) {
        this.restart = restart;
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
        this.red = red;
        this.blue = blue;
        return this;
    }
    
    public boolean isReady() {
        return mode != null && grid != null;
    }
    
    private boolean isNewGameChoices() {
        return mode == ControlMode.CHOOSE_OPPONENT || mode == ControlMode.CHOOSE_DIFFICULTY;
    }
    
    public void startGame(Team team) {
        grid.state().menu().menu();
        mode = ControlMode.GAME;
        grid.ctrl().orders().add(0, new PassTurn());
        
        if (team == null) {
            String teamName = Team.RED.toString();
            if (Math.random() > .5) {
                teamName = Team.BLUE.toString();
                grid.ctrl().orders().add(0, new PassTurn());
            }
            grid.state().menu().message = teamName + " first";
        } else {
            grid.ctrl().orders().add(0, new PassTurn());
        }
    }
    
    private boolean controllersAreSetUp() {
        return choseCPUOpponent != null;
    }
    
    private boolean isMenuConfirm() {
        return mode == ControlMode.CONFIRM_NEW_GAME || mode == ControlMode.CONFIRM_SAVE
                || mode == ControlMode.CONFIRM_LOAD || mode == ControlMode.CONFIRM_QUIT;
    }
    
    public void loadOpponent() {
        IController blue;
        if (grid.opponentWasCPU()) {
            if (grid.opponentLevel() == 0) {
                blue = new ComputerPlayerBasic(pool).grid(grid).team(Team.BLUE);
            } else {
                blue = new ComputerPlayerMid2(pool, grid, Team.BLUE, grid.opponentLevel());
            }
        } else {
            blue = this;
        }
        
        setControllers(this, blue);
    }
    
    public void processInput() throws CheeseException {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) {
            Simulator.simulate(pool);
            
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F1)) {
            java.lang.System.out.println(grid.recording().toString());
        }
        
        if (isNewGameChoices()) {
            
            // CHOOSE OPPONENT
            if (!controllersAreSetUp()) {
                
                if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                    // human
                    setControllers(this, this);
                    choseCPUOpponent = false;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
                    // cpu
                    choseCPUOpponent = true;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
                    // cpu vs cpu
                    setControllers(new ComputerPlayerBasic(pool).grid(grid).team(Team.RED),
                            new ComputerPlayerMid2(pool, grid, Team.BLUE, 4));
                    choseCPUOpponent = false;
                }
                if (controllersAreSetUp()) {
                    if (choseCPUOpponent) {
                        mode = ControlMode.CHOOSE_DIFFICULTY;
                        grid.state().menu().chooseDifficulty();
                    } else {
                        startGame(null);
                    }
                }
            } else {
                // CHOOSE DIFFICULTY OR START
                boolean chose = false;
                
                if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
                    chose = true;
                    setControllers(this, new ComputerPlayerMid2(pool, grid, Team.BLUE, 4));
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
                    chose = true;
                    setControllers(this, new ComputerPlayerMid2(pool, grid, Team.BLUE, 2));
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                    setControllers(this, new ComputerPlayerBasic(pool).grid(grid).team(Team.BLUE));
                    chose = true;
                }
                
                if (chose)
                    startGame(null);
            }
        } else if (isMenuConfirm()) {
            // CONFIRM MENU CHOICES
            
            boolean saidYes = Gdx.input.isKeyJustPressed(Input.Keys.Y);
            boolean saidNo = Gdx.input.isKeyJustPressed(Input.Keys.N);
            
            if (saidNo) {
                mode = ControlMode.GAME;
                grid.state().menu().menu();
            } else if (saidYes) {
                if (mode == ControlMode.CONFIRM_NEW_GAME) {
                    this.restart.action(null);
                    return;
                } else if (mode == ControlMode.CONFIRM_SAVE) {
                    Board.saveGame(grid, blue);
                } else if (mode == ControlMode.CONFIRM_LOAD) {
                    CheeseGrid load = Board.loadFromSave();
                    if (load != null) {
                        this.restart.action(load);
                    }
                } else if (mode == ControlMode.CONFIRM_QUIT) {
                    try {
                        dao.dump();
                    } catch (IOException e) {
                        throw new CheeseException(e);
                    }
                    System.exit(0);
                }
                mode = ControlMode.GAME;
                grid.state().menu().menu();
            }
        } else if (mode == ControlMode.GAME_OVER) {
            // GAME OVER
            
            boolean saidYes = Gdx.input.isKeyJustPressed(Input.Keys.Y);
            boolean saidNo = Gdx.input.isKeyJustPressed(Input.Keys.N);
            
            if (saidYes) {
                this.restart.action(null);
            } else if (saidNo) {
                System.exit(0);
            }
        } else if (mode == ControlMode.GAME) {
            // PLAY THE GAME
            
            Scores scores = GridController.scores(grid, false);
            boolean unprocessedOrdersExist = grid.ctrl().orders().size() > 0;
            boolean gameIsOver = (scores.redScore == grid.micePerTeam() || scores.blueScore == grid.micePerTeam())
                    && !unprocessedOrdersExist;
            
            if (gameIsOver) {
                Team winner = scores.redScore > scores.blueScore ? Team.RED : Team.BLUE;
                
                mode = ControlMode.GAME_OVER;
                grid.state().menu().gameOverWithWinner(winner);
            } else {
                if (unprocessedOrdersExist) {
                    grid.ctrl().executeNext();
                } else {
                    IOrder move = grid.activeTeam() == Team.RED ? red.getOrder() : blue.getOrder();
                    if (move != null) {
                        // String board = Board.getBoardString(grid,
                        // false).trim();
                        // Integer value = dao.valueOf(board);
                        // if (value != null)
                        // dao.insert(board, value);
                        
                        grid.ctrl().orders().add(move);
                    }
                    
                    if (move == null || move.type() == OrderType.PROGRESS)
                        grid.state().randomMouseEatsCheese();
                }
            }
        }
    }
    
    @Override
    public IOrder getOrder() {
        
        int x = grid.activePole();
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            return new ColumnShift(x, Direction.UP);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            return new ColumnShift(x, Direction.DOWN);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (x - 1 >= 0)
                return new SetHand(grid, -1, true);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (x - 1 <= grid.wMax())
                return new SetHand(grid, 1, true);
        }
        
        // USER PRESSED A MENU KEY
        if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            mode = ControlMode.CONFIRM_NEW_GAME;
            grid.state().menu().confirmNewGame();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            mode = ControlMode.CONFIRM_SAVE;
            grid.state().menu().confirmSave();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            mode = ControlMode.CONFIRM_LOAD;
            grid.state().menu().confirmLoad();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            mode = ControlMode.CONFIRM_QUIT;
            grid.state().menu().confirmQuit();
        }
        
        return null;
    }
    
}
