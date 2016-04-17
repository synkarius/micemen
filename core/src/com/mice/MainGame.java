package com.mice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import graphical.GridGfx;
import graphical.SceneGraph;
import model.CheeseException;
import model.CheeseGrid;

public class MainGame extends ApplicationAdapter {
    SpriteBatch batch;
    CheeseGrid  grid;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        
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
    }
}
