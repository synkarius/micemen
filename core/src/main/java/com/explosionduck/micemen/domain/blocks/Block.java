package com.explosionduck.micemen.domain.blocks;

import com.explosionduck.micemen.fx.MouseGraphic;
import com.explosionduck.micemen.util.IdGenerator;

public abstract class Block {

    private final long id;
    private final BlockType blockType;
    private MouseGraphic graphic;

    protected Block(BlockType blockType) {
        this.id = IdGenerator.createId();
        this.blockType = blockType;
        this.graphic = null;
    }

    public BlockType getBlockType() {
        return blockType;
    }

    public MouseGraphic getGraphic() {
        return graphic;
    }

    public void setGraphic(MouseGraphic graphic) {
        this.graphic = graphic;
    }

    public boolean isMouse() {
        return blockType == BlockType.MOUSE;
    }

    public boolean isRedMouse() {
        return isMouse() && ((Mouse) this).getTeam() == Team.RED;
    }

    public boolean isBlueMouse() {
        return isMouse() && ((Mouse) this).getTeam() == Team.BLUE;
    }

    public boolean isTeam(Team team) {
        return isMouse() && ((Mouse) this).getTeam() == team;
    }

    public boolean isEmpty() {
        return this.blockType == BlockType.EMPTY;
    }

    public boolean isCheese() {
        return this.blockType == BlockType.CHEESE;
    }

    @Override
    public String toString() {
        return this.blockType.toString().toLowerCase();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Block block = (Block) o;

        return id == block.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }
}