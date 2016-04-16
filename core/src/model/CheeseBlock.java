package model;

import entity.sim.Block;

public class CheeseBlock extends Block {
	public CheeseBlock(CheeseGrid grid) {
		super(grid);
		this.type = Type.CHEESE;
	}
}
