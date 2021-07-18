package com.explosionduck.micemen.fx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.screens.game.GameScreen;

import java.util.HashMap;
import java.util.Map;

import static com.explosionduck.micemen.fx.Textures.BLUES_TEX;
import static com.explosionduck.micemen.fx.Textures.REDS_TEX;

public class DefaultMouseTexturesMap implements MouseTexturesMap {

    private final Map<MouseGraphic, Map<Team, TextureRegion>> textureRegions;

    public DefaultMouseTexturesMap() {
        this.textureRegions = new HashMap<>();
        for (var graphic : MouseGraphic.values()) {
            var graphicToTeamsAndTextures = new HashMap<Team, TextureRegion>();
            for (var team : Team.values()) {
                var texture = new TextureRegion(
                        Team.RED == team ? REDS_TEX : BLUES_TEX,
                        graphic.getGraphicX() * GameScreen.BLOCK_SIZE,
                        graphic.getGraphicY() * GameScreen.BLOCK_SIZE,
                        GameScreen.BLOCK_SIZE,
                        GameScreen.BLOCK_SIZE);
                graphicToTeamsAndTextures.put(team, texture);
            }
            this.textureRegions.put(graphic, graphicToTeamsAndTextures);
        }
    }

    @Override
    public TextureRegion getTextureRegion(MouseGraphic graphic, Team team) {
        return this.textureRegions.get(graphic).get(team);
    }
}
