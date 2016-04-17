package com.mice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;

import graphical.GridGfx;
import graphical.SceneGraph;
import model.CheeseException;
import model.CheeseGrid;

public class MainGame extends ApplicationAdapter {
    ShapeRenderer      shaper;
    SpriteBatch        batch;
    CheeseGrid         grid;
    OrthographicCamera camera;
    BitmapFont         font;
    FitViewport        viewport;
    
    @Override
    public void create() {
        
        batch = new SpriteBatch();
        camera = new OrthographicCamera(SceneGraph.WIDTH, SceneGraph.HEIGHT);
        viewport = new FitViewport(SceneGraph.WIDTH, SceneGraph.HEIGHT, camera);
        viewport.apply();
        
        
        shaper = new ShapeRenderer();
        shaper.setProjectionMatrix(batch.getProjectionMatrix());
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        
        grid = new CheeseGrid(21, 13, 12);
        try {
            grid.ctrl().fillGrid();
            grid.ctrl().recalculateMoves();
            grid.ctrl().executeAll();
            grid.makeGraphical();
        } catch (CheeseException e) {
            e.printStackTrace();
        }
        
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
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
