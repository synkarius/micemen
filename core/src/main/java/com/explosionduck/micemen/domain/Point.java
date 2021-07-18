package com.explosionduck.micemen.domain;

public class Point {

    public int x;
    public int y;
    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Point add(Point other) {
        return new Point(x + other.x, y + other.y);
    }

}
