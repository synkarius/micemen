package com.explosionduck.micemen.domain;

public class Decision {

    private final int x;
    private final Direction direction;

    public Decision(int x, Direction direction) {
        this.x = x;
        this.direction = direction;
    }


    public int getX() {
        return x;
    }

    public Direction getDirection() {
        return direction;
    }
}
