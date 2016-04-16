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

	private static final TextureRegion redStand = new TextureRegion(reds, 0, 0, 25, 25);
	private static final TextureRegion blueStand = new TextureRegion(blues, 0, 0, 25, 25);

	public SceneGraph() {
	}

	public static void drawGrid(CheeseGrid grid, SpriteBatch batch) {
		for (int x = 0; x < grid.width(); x++) {
			for (int y = 0; y < grid.height(); y++) {
				Block block = grid.get(x, y);

				/**
				 * Note to self: fillgrid() put blue mice on the red side --
				 * WTF?? Also red mice in column 0
				 */

				if (block.isRedMouse()) {
					batch.draw(redStand, x * BLOCK_SIZE, y * BLOCK_SIZE + 10);
				} else if (block.isBlueMouse()) {
					batch.draw(blueStand, x * BLOCK_SIZE, y * BLOCK_SIZE + 8);
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
