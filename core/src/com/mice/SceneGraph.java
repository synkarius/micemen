package com.mice;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import entity.sim.Block;
import model.CheeseGrid;

public class SceneGraph {

	public static final int BLOCK_SIZE = 25;

	public SceneGraph() {

	}

	public static void drawGrid(CheeseGrid grid, SpriteBatch batch) {
		for (int x = 0; x < grid.width(); x++) {
			for (int y = 0; y < grid.height(); y++) {
				Block block = grid.get(x, y);

				Texture texture;
				if (block.isRedMouse()) {
					texture = new Texture("redmice.png");
				} else if (block.isBlueMouse()) {
					texture = new Texture("bluemice.png");
				} else if (block.isCheese()) {
					texture = new Texture("cheese.png");
				} else {
					/** no need to draw empty blocks */
					continue;
				}

				batch.draw(texture, x * BLOCK_SIZE, y * BLOCK_SIZE);
			}
		}
	}
}
