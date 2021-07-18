package com.explosionduck.micemen.fx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.explosionduck.micemen.domain.blocks.Team;

public interface MouseTexturesMap {

    TextureRegion getTextureRegion(MouseGraphic graphic, Team team);
}
