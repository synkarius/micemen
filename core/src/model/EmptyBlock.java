package model;

import entity.sim.Block;

public class EmptyBlock extends Block {
	public EmptyBlock() {
		super();
		this.type = Type.EMPTY;
	}
	
	@Override
	public Block copy() {
		return new EmptyBlock();
	}
}
