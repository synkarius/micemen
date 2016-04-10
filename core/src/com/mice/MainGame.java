package com.mice;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import model.CheeseException;
import model.CheeseGrid;

public class MainGame extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	
	CheeseGrid grid;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("bg.png");
		
		grid = new CheeseGrid(21, 13, 12);
		try {
			grid.ctrl().fillGrid();
		} catch (CheeseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		SceneGraph.drawGrid(grid, batch);
		batch.end();
	}
}
