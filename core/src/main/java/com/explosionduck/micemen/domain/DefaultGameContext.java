package com.explosionduck.micemen.domain;

import com.explosionduck.micemen.control.Controller;
import com.explosionduck.micemen.control.keyboard.modes.ControlModeType;
import com.explosionduck.micemen.fx.DefaultFrameMap;
import com.explosionduck.micemen.fx.FrameMap;

public class DefaultGameContext implements GameContext {

    // the main grid
    private CheeseGrid grid;

    // game setup
    private OpponentMode opponentMode;
    private ControlModeType controlModeType;
    private Difficulty difficulty;

    // game mode controllers
    private Controller redController;
    private Controller blueController;

    // menu bar messages
    private String message;
    private String text;

    // scores
    private int redScore;
    private int blueScore;

    // column shifting
    private Integer columnShifting;
    private int yOffset;
    private float redHandYOffset;
    private float blueHandYOffset;

    // progress
    private double thinkingReady;
    private double thinkingTotal;

    // animation map : contents are mutable but map reference isn't
    private final FrameMap frameMap;

    public DefaultGameContext() {
        this.frameMap = new DefaultFrameMap();
    }

    public CheeseGrid getGrid() {
        return grid;
    }

    public void setGrid(CheeseGrid grid) {
        this.grid = grid;
    }

    public OpponentMode getOpponentMode() {
        return opponentMode;
    }

    public void setOpponentMode(OpponentMode opponentMode) {
        this.opponentMode = opponentMode;
    }

    public ControlModeType getControlModeType() {
        return controlModeType;
    }

    public void setControlModeType(ControlModeType controlModeType) {
        this.controlModeType = controlModeType;
    }

    public Difficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Controller getRedController() {
        return redController;
    }

    public void setRedController(Controller redController) {
        this.redController = redController;
    }

    public Controller getBlueController() {
        return blueController;
    }

    public void setBlueController(Controller blueController) {
        this.blueController = blueController;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getRedScore() {
        return String.valueOf(redScore);
    }

    public void setRedScore(int redScore) {
        this.redScore = redScore;
    }

    public String getBlueScore() {
        return String.valueOf(blueScore);
    }

    public void setBlueScore(int blueScore) {
        this.blueScore = blueScore;
    }

    public Integer getColumnShifting() {
        return columnShifting;
    }

    public void setColumnShifting(Integer columnShifting) {
        this.columnShifting = columnShifting;
    }

    public int getYOffset() {
        return yOffset;
    }

    public void setYOffset(int yOffset) {
        this.yOffset = yOffset;
    }

    public float getRedHandYOffset() {
        return redHandYOffset;
    }

    public void setRedHandYOffset(float redHandYOffset) {
        this.redHandYOffset = redHandYOffset;
    }

    public float getBlueHandYOffset() {
        return blueHandYOffset;
    }

    public void setBlueHandYOffset(float blueHandYOffset) {
        this.blueHandYOffset = blueHandYOffset;
    }

    public double getThinkingReady() {
        return thinkingReady;
    }

    public void setThinkingReady(double thinkingReady) {
        this.thinkingReady = thinkingReady;
    }

    public double getThinkingTotal() {
        return thinkingTotal;
    }

    public void setThinkingTotal(double thinkingTotal) {
        this.thinkingTotal = thinkingTotal;
    }

    public FrameMap getAnimationFrameMap() {
        return frameMap;
    }
}
