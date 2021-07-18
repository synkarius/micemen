package com.explosionduck.micemen.screens;

public interface Screen {

    void draw();

    boolean isActive();

    ScreenType getType();
}
