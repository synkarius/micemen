package com.explosionduck.micemen;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.explosionduck.micemen.calculation.BoardGenerator;
import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.*;
import com.explosionduck.micemen.command.menu.ChangeModeCommand;
import com.explosionduck.micemen.command.menu.EnableEffectsCommand;
import com.explosionduck.micemen.command.menu.EvaluateBoardCommand;
import com.explosionduck.micemen.command.menu.InitialTurnCommand;
import com.explosionduck.micemen.control.Controller;
import com.explosionduck.micemen.control.keyboard.KeyboardController;
import com.explosionduck.micemen.control.keyboard.modes.*;
import com.explosionduck.micemen.dagger.config.DaggerPostLibGDXInitComponent;
import com.explosionduck.micemen.dagger.config.DaggerPreLibGDXInitComponent;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.DefaultGameContext;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.fx.FrameMap;
import com.explosionduck.micemen.fx.MenuText;
import com.explosionduck.micemen.screens.Screen;
import com.explosionduck.micemen.screens.ScreenType;
import com.explosionduck.micemen.screens.game.GameScreen;
import com.explosionduck.micemen.screens.game.GameScreenRenderer;
import com.explosionduck.micemen.screens.title.TitleScreen;

import javax.inject.Inject;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;

public class MainGame extends ApplicationAdapter {

    private final ExecutorService executorService;
    private final GridCalculator gridCalculator;
    private final BoardGenerator boardGenerator;
    private final BoardScorer boardScorer;

    /** must be instantiated at "create" time, not in the constructor */
    private FitViewport fitViewport;
    /** must be instantiated at "create" time, not in the constructor */
    private GameScreenRenderer gameScreenRenderer;
    /** must be instantiated at "create" time, not in the constructor */
    private TitleScreen titleScreen;

    private CheeseGrid grid;
    private GameContext gameContext;
    private Queue<Screen> screens;
    private CommandExecutor commandExecutor;

    @Inject
    public MainGame(
            ExecutorService executorService,
            GridCalculator gridCalculator,
            BoardGenerator boardGenerator,
            BoardScorer boardScorer) {
        this.executorService = executorService;
        this.gridCalculator = gridCalculator;
        this.boardGenerator = boardGenerator;
        this.boardScorer = boardScorer;
    }

    @Override
    public void create() {
        // libgdx screen setup
        var injectorComponent = DaggerPostLibGDXInitComponent.create();
        this.fitViewport = injectorComponent.buildFitViewport();
        this.fitViewport.apply();
        this.titleScreen = injectorComponent.buildTitleScreen();
        this.gameScreenRenderer = injectorComponent.buildGameScreenRenderer();
        this.screens = new LinkedList<>();
        this.screens.add(this.titleScreen);

        // start game
        this.restart(null);
    }

    @Override
    public void render() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.F12)) { // TODO: move this simulator stuff
            DaggerPreLibGDXInitComponent.create()
                    .buildSimulator()
                    .simulate();
        }

        var screen = this.screens.element();
        if (screen != this.titleScreen) {
            if (this.commandExecutor.hasCommands()) {
                this.commandExecutor.executeNextCommand(this.gameContext);
            } else {
                var commands = grid.getActiveTeam() == Team.BLUE
                        ? this.gameContext.getBlueController().getCommands()
                        : this.gameContext.getRedController().getCommands();
                this.commandExecutor.addCommands(commands);

                if (commands.stream()
                        .map(Command::getType)
                        .anyMatch(CommandType.PROGRESS::equals)) {
                    FrameMap.randomMouseEatsCheese(grid);
                }
            }
        }

        // draw the screen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (screen.isActive()) {
            screen.draw();
        } else {
            // title screen sequence has ended
            this.screens.poll();
        }
    }

    @Override
    public void resize(int width, int height) {
        this.fitViewport.update(width, height);
    }

    private NewGameBootstrapper restart(CheeseGrid loadedGrid) {
        // reset command executor and game context
        this.commandExecutor = new DefaultCommandExecutor();
        this.commandExecutor.addCommand(loadedGrid == null
                ? new ChangeModeCommand(ControlModeType.CHOOSE_OPPONENT, MenuText.CHOOSE_OPPONENT)
                : new ChangeModeCommand(ControlModeType.GAME, MenuText.STANDARD_OPTIONS));
        this.gameContext = new DefaultGameContext();
        this.screens.removeIf(screen -> ScreenType.GAME == screen.getType());
        this.screens.add(new GameScreen(this.gameScreenRenderer, this.gameContext));

        // set up grid
        if (loadedGrid != null) {
            this.grid = loadedGrid;
        } else {
            this.grid = CheeseGrid.getNewDefault();
            this.boardGenerator.fillGrid(this.grid);
        }
//        this.grid.makeGraphical();
        this.gameContext.setGrid(this.grid);

        // do initial gravity and mice-walking-forward actions offscreen
        var newCommands = this.gridCalculator.calculateNewCommands(this.grid);
        this.commandExecutor.addCommands(newCommands);
        this.commandExecutor.executeAll(this.gameContext);

        // inputs
        var keyboardController = new KeyboardController(this.gameContext, List.of(
                new ChooseOpponentMode(),
                new ChooseDifficultyMode(executorService, gridCalculator, grid),
                new GameMode(gridCalculator, grid),
                new ConfirmNewGameMode(this::restart),
                new ConfirmLoadMode(this::restart, boardScorer, executorService, gridCalculator),
                new ConfirmSaveMode(),
                new ConfirmQuitMode(),
                new GameOverMode(this::restart)));
        // initially setting both controllers to keyboard; changed later if the player so chooses
        this.gameContext.setRedController(keyboardController);
        this.gameContext.setBlueController(keyboardController);

        if (loadedGrid != null) {
//            this.loadGame(keyboardController);
            this.commandExecutor.addCommand(new InitialTurnCommand(this.grid.getActiveTeam()));
        }
        this.commandExecutor.addCommand(new EvaluateBoardCommand(this.boardScorer));
        this.commandExecutor.addCommand(new EnableEffectsCommand());

        return new NewGameBootstrapper() {
            @Override
            public void addCommands(Collection<Command> commands) {
                MainGame.this.commandExecutor.addCommands(commands);
            }

            @Override
            public void setBlueController(Controller controller) {
                MainGame.this.gameContext.setBlueController(controller);
            }
        };
    }
}
