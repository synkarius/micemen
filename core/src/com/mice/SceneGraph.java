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
	private static final Texture cheeseTex = new Texture("cheese.png");
	
	private static final TextureRegion cheese = new TextureRegion(cheeseTex, 0, 0, 25, 25);
	private static final TextureRegion redStand = new TextureRegion(reds, 0, 0, 25, 25);
	private static final TextureRegion blueStand = new TextureRegion(blues, 0, 0, 25, 25);

	public SceneGraph() {
	}
	
	public static int flipY(CheeseGrid grid, int y) {
		return Math.abs(grid.height() - y - 1);
	}
	
	public static void drawGrid(CheeseGrid grid, SpriteBatch batch) {
		for (int x = 0; x < grid.width(); x++) {
			for (int y = 0; y < grid.height(); y++) {
				Block block = grid.get(x, y);
				
				TextureRegion region = null;
				int _y = flipY(grid, y);

				if (block.isRedMouse()) {
					region = redStand;
				} else if (block.isBlueMouse()) {
					region = blueStand;
				} else if (block.isCheese()) {
					region = cheese;
				} else {
					/** no need to draw empty blocks */
					continue;
				}
				
				batch.draw(region, x * BLOCK_SIZE, _y * BLOCK_SIZE);
			}
		}
	}
}
