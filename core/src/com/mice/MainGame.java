package com.mice;

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
import graphical.GridGfx;
import graphical.SceneGraph;
import model.CheeseException;
import model.CheeseGrid;
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
        
        this.input = new KeyboardController().setMode(mode).setGrid(this.grid).setRestart(this::restart);
        
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
            this.grid.ctrl().valueBoard();
        } catch (CheeseException e) {
            log.log(Level.SEVERE, "Grid setup failure", e);
        }
    }
    
    @Override
    public void create() {
        {
            /** libgdx screen setup */
            batch = new SpriteBatch();
            camera = new OrthographicCamera(SceneGraph.WIDTH, SceneGraph.HEIGHT);
            viewport = new FitViewport(SceneGraph.WIDTH, SceneGraph.HEIGHT, camera);
            viewport.apply();
            shaper = new ShapeRenderer();
            shaper.setProjectionMatrix(batch.getProjectionMatrix());
            font = new BitmapFont();
            font.setColor(Color.WHITE);
        }
        
        Restart restart = this::restart;
        restart.action(null);
    }
    
    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        
        batch.begin();
        batch.draw(GridGfx.bg, SceneGraph.X_OFFSET, SceneGraph.Y_OFFSET);
        SceneGraph.drawGrid(grid, batch);
        batch.end();
        
        SceneGraph.drawBoxes(shaper);
        
        batch.begin();
        SceneGraph.drawControls(grid, batch);
        SceneGraph.drawText(grid, batch, font);
        batch.end();
        
        try {
            input.processInput();
        } catch (CheeseException e) {
            log.log(Level.SEVERE, "Input processing failure.", e);
        }
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
