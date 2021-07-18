package com.explosionduck.micemen.domain.blocks;

import com.explosionduck.micemen.fx.MouseGraphic;

public class Mouse extends Block {

    private final Team team;

    public Mouse(Team team) {
        super(BlockType.MOUSE);
        this.team = team;
        this.setGraphic(MouseGraphic.STAND);
    }

    public Team getTeam() {
        return team;
    }

}
