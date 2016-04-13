package com.mice;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import entity.sim.Block;
import model.CheeseGrid;

public class SceneGraph {

	public static final int BLOCK_SIZE = 25;
	
	private static final Texture reds = new Texture("redmice.png");
	private static final Texture blues = new Texture("bluemice.png");
	private static final Texture cheese = new Texture("cheese.png");
	
	private TextureRegion redStand;

	public SceneGraph() {
	}

	public static void drawGrid(CheeseGrid grid, SpriteBatch batch) {
		for (int x = 0; x < grid.width(); x++) {
			for (int y = 0; y < grid.height(); y++) {
				Block block = grid.get(x, y);

				TextureRegion region;
				if (block.isRedMouse()) {
					region = new TextureRegion(reds, 0, 0, 25, 25);
					batch.draw(region, x * BLOCK_SIZE, y * BLOCK_SIZE + 10);
				} else if (block.isBlueMouse()) {
					region = new TextureRegion(blues, 0, 0, 25, 25);
					batch.draw(region, x * BLOCK_SIZE, y * BLOCK_SIZE + 8);
				} else if (block.isCheese()) {
					batch.draw(cheese, x * BLOCK_SIZE, y * BLOCK_SIZE);
				} else {
					/** no need to draw empty blocks */
					continue;
				}

				
			}
		}
	}
}
