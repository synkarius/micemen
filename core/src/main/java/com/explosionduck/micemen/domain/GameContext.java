package com.explosionduck.micemen.domain;

import com.explosionduck.micemen.control.Controller;
import com.explosionduck.micemen.control.keyboard.modes.ControlModeType;
import com.explosionduck.micemen.fx.FrameMap;

public interface GameContext {

    CheeseGrid getGrid();

    void setGrid(CheeseGrid grid);

    OpponentMode getOpponentMode();

    void setOpponentMode(OpponentMode opponentMode);

    ControlModeType getControlModeType();

    void setControlModeType(ControlModeType controlModeType);

    Difficulty getDifficulty();

    void setDifficulty(Difficulty difficulty);

    Controller getRedController();

    void setRedController(Controller redController);

    Controller getBlueController();

    void setBlueController(Controller blueController);

    String getMessage();

    void setMessage(String message);

    String getText();

    void setText(String text);

    String getRedScore();

    void setRedScore(int redScore);

    String getBlueScore();

    void setBlueScore(int blueScore);

    Integer getColumnShifting();

    void setColumnShifting(Integer columnShifting);

    int getYOffset();

    void setYOffset(int yOffset);

    float getRedHandYOffset();

    void setRedHandYOffset(float redHandYOffset);

    float getBlueHandYOffset();

    void setBlueHandYOffset(float blueHandYOffset);

    double getThinkingReady();

    void setThinkingReady(double thinkingReady);

    double getThinkingTotal();

    void setThinkingTotal(double thinkingTotal);

    FrameMap getAnimationFrameMap();
}
