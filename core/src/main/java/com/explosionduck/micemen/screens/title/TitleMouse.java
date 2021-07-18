package com.explosionduck.micemen.screens.title;

import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.fx.MouseGraphic;

import java.util.HashMap;
import java.util.Map;

class TitleMouse {

    private final Team team;
    private MouseGraphic graphic;
    float alpha;
    Map<Integer, TitleScreenChange> changes = new HashMap<>();
    int x;
    int y;

    TitleMouse(int x, int y, Team team, MouseGraphic graphic) {
        this.x = x;
        this.y = y;
        this.team = team;
        this.graphic = graphic;
    }

    void play(int time) {
        TitleScreenChange action = changes.get(time);
        if (action == null)
            return;

        switch (action) {
            case DOWN:
                move(-1, 0);
                break;
            case UP:
                move(1, 0);
                break;
            case LEFT:
                move(0, -1);
                break;
            case RIGHT:
                move(0, 1);
                break;
            case FALL:
                graphic = MouseGraphic.FALL;
                break;
            case UMBRELLA:
                graphic = MouseGraphic.UMBRELLA;
                break;
            case FACE_CAMERA:
                graphic = MouseGraphic.FACE_CAMERA;
                break;
            case WALK:
                graphic = MouseGraphic.WALK;
                break;
            case VISIBLE:
                alpha = 1;
                break;
        }
    }

    void move(int updown, int leftright) {
        y += updown;
        if (y == TitleScreen.DOWN - 2)
            alpha = (float) .5;
        else if (y == TitleScreen.DOWN - 4)
            alpha = 1;
        if (y < 1) {
            y = 1;
            graphic = MouseGraphic.STAND;
        }

        x += leftright;
    }

    Team team() {
        return team;
    }

    MouseGraphic graphic() {
        return graphic;
    }
}
