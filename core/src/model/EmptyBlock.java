package model;

public class EmptyBlock extends Block {
    public EmptyBlock(CheeseGrid grid) {
        super(grid);
        this.type = Type.EMPTY;
    }
}
