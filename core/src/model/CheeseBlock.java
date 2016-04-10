package model;

import entity.sim.Block;

public class CheeseBlock extends Block {
	public CheeseBlock() {
		super();
		this.type = Type.CHEESE;
	}
	
	@Override
	public Block copy() {
		return new CheeseBlock();
	}
}
