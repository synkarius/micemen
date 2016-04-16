package model;

import entity.sim.Block;

public class EmptyBlock extends Block {
	public EmptyBlock(CheeseGrid grid) {
		super(grid);
		this.type = Type.EMPTY;
	}
}
