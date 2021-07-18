package com.explosionduck.micemen.fx;

public enum MouseGraphic {
    STAND(0, 0),
    WALK(1, 0),
    EAT1(2, 0),
    EAT2(3, 0),
    EAT3(0, 1),
    POINT(1, 1),
    UMBRELLA(2, 1),
    FACE_CAMERA(3, 1),
    MUSCLE1(0, 2),
    MUSCLE2(1, 2),
    FALL(2, 2);

    private final int graphicX;
    private final int graphicY;

    MouseGraphic(int graphicX, int graphicY) {
        this.graphicX = graphicX;
        this.graphicY = graphicY;
    }

    public int getGraphicX() {
        return graphicX;
    }

    public int getGraphicY() {
        return graphicY;
    }
}