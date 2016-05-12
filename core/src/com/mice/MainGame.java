package com.mice;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import control.KeyboardController;
import control.KeyboardController.ControlMode;
import data.DataAccessor;
import graphical.Resource;
import graphical.SceneGraph;
import graphical.TitleScreen;
import model.CheeseException;
import model.CheeseGrid;
import model.SimPoint;
import util.Restart;

public class MainGame extends ApplicationAdapter {
    private static final Logger log = Logger.getLogger(MainGame.class.getName());
    
    ShapeRenderer               shaper;
    SpriteBatch                 batch;
    OrthographicCamera          camera;
    BitmapFont                  font;
    FitViewport                 viewport;
    
    CheeseGrid                  grid;
    KeyboardController          input;
    
    DataAccessor                dao;
    ExecutorService             pool;
    
    public void restart(CheeseGrid grid) {
        ControlMode mode;
        boolean loaded = grid != null;
        if (loaded) {
            mode = ControlMode.GAME;
            this.grid = grid;
        } else { // new game from scratch
            mode = ControlMode.CHOOSE_OPPONENT;
            this.grid = CheeseGrid.getNewDefault();
        }
        
        this.input = new KeyboardController(pool, dao).setMode(mode).setGrid(this.grid).setRestart(this::restart);
        this.grid.recording().startRecording();
        
        try {
            if (!loaded)
                this.grid.ctrl().fillGrid();
            this.grid.ctrl().recalculateMoves();
            this.grid.ctrl().executeAll();
            this.grid.makeGraphical();
            if (!loaded) {
                this.grid.state().menu().chooseOpponent();
            } else {
                this.grid.state().menu().menu();
                this.input.loadOpponent();
                this.input.startGame(this.grid.activeTeam());
            }
            this.grid.ctrl().valueBoard(false);
        } catch (CheeseException e) {
            log.log(Level.SEVERE, "Grid setup failure", e);
        }
    }
    
    @Override
    public void create() {
        {
            /** libgdx screen setup */
            pool = Executors.newFixedThreadPool(1);
            batch = new SpriteBatch();
            camera = new OrthographicCamera(SceneGraph.WIDTH, SceneGraph.HEIGHT);
            viewport = new FitViewport(SceneGraph.WIDTH, SceneGraph.HEIGHT, camera);
            viewport.apply();
            shaper = new ShapeRenderer();
            shaper.setProjectionMatrix(batch.getProjectionMatrix());
            font = new BitmapFont();
            font.setColor(Color.WHITE);
        }
        
        dao = new DataAccessor();
        dao.create();
        
        
        Restart restart = this::restart;
        restart.action(null);
        
        
    }
    
    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        if (!TitleScreen.isFinished()) {
            batch.begin();
            TitleScreen.draw(batch);
            batch.end();
        } else {
            
            batch.begin();
            batch.draw(Resource.bg, SceneGraph.X_OFFSET, SceneGraph.Y_OFFSET);
            SimPoint escapee = SceneGraph.drawGrid(grid, batch);
            batch.end();
            
            SceneGraph.drawBoxes(shaper);
            
            batch.begin();
            SceneGraph.drawControls(grid, batch);
            SceneGraph.drawText(grid, batch, font);
            if (escapee != null)
                SceneGraph.drawEscapee(batch, escapee);
            if (grid.state().thinkingTotal != 0)
                SceneGraph.drawThinking(grid, batch, font, shaper);
            batch.end();
            
            try {
                input.processInput();
            } catch (CheeseException e) {
                log.log(Level.SEVERE, "Input processing failure.", e);
            }
        }
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
