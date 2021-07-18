package com.explosionduck.micemen.calculation.noop;

import com.explosionduck.micemen.control.Controller;
import com.explosionduck.micemen.control.keyboard.modes.ControlModeType;
import com.explosionduck.micemen.domain.*;
import com.explosionduck.micemen.fx.FrameMap;

public class NoOpGameContext implements GameContext {

    private static final FrameMap NO_OP_FRAME_MAP = new NoOpFrameMap();

    private final FrameMap noOpFrameMap;
    private final CheeseGrid simGrid;

    public NoOpGameContext(CheeseGrid simGrid) {
        this.noOpFrameMap = NO_OP_FRAME_MAP;
        this.simGrid = simGrid;
    }

    @Override
    public CheeseGrid getGrid() {
        return this.simGrid;
    }

    @Override
    public void setGrid(CheeseGrid grid) {
        throw new CheeseException("One grid per " + NoOpGameContext.class.getName());
    }

    @Override
    public OpponentMode getOpponentMode() {
        return null;
    }

    @Override
    public void setOpponentMode(OpponentMode opponentMode) {}

    @Override
    public ControlModeType getControlModeType() {
        return null;
    }

    @Override
    public void setControlModeType(ControlModeType controlModeType) {}

    @Override
    public Difficulty getDifficulty() {
        return null;
    }

    @Override
    public void setDifficulty(Difficulty difficulty) {

    }

    @Override
    public Controller getRedController() {
        return null;
    }

    @Override
    public void setRedController(Controller redController) {

    }

    @Override
    public Controller getBlueController() {
        return null;
    }

    @Override
    public void setBlueController(Controller blueController) {

    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public void setMessage(String message) {

    }

    @Override
    public String getText() {
        return null;
    }

    @Override
    public void setText(String text) {

    }

    @Override
    public String getRedScore() {
        return null;
    }

    @Override
    public void setRedScore(int redScore) {

    }

    @Override
    public String getBlueScore() {
        return null;
    }

    @Override
    public void setBlueScore(int blueScore) {

    }

    @Override
    public Integer getColumnShifting() {
        return null;
    }

    @Override
    public void setColumnShifting(Integer columnShifting) {

    }

    @Override
    public int getYOffset() {
        return 0;
    }

    @Override
    public void setYOffset(int yOffset) {

    }

    @Override
    public float getRedHandYOffset() {
        return 0;
    }

    @Override
    public void setRedHandYOffset(float redHandYOffset) {

    }

    @Override
    public float getBlueHandYOffset() {
        return 0;
    }

    @Override
    public void setBlueHandYOffset(float blueHandYOffset) {

    }

    @Override
    public double getThinkingReady() {
        return 0;
    }

    @Override
    public void setThinkingReady(double thinkingReady) {

    }

    @Override
    public double getThinkingTotal() {
        return 0;
    }

    @Override
    public void setThinkingTotal(double thinkingTotal) {

    }

    @Override
    public FrameMap getAnimationFrameMap() {
        return this.noOpFrameMap;
    }
}
