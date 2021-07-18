package com.explosionduck.micemen.fx;

public enum MenuText implements LeftSideMessage {

    DEFAULT(""),
    STANDARD_OPTIONS("<N>ew   <L>oad   <S>ave   <Q>uit"),
    CONFIRM_QUIT("Really Quit? [Y/N]"),
    CONFIRM_NEW_GAME("Really Start New Game? [Y/N]"),
    CONFIRM_SAVE("Really Save? [Y/N]"),
    CONFIRM_LOAD("Really Load? [Y/N]"),
    CHOOSE_DIFFICULTY("Difficulty:   <H>ard    <M>edium    <E>asy"),
    CHOOSE_OPPONENT("Play Against:   <H>uman   <C>omputer"),
    CPU_BATTLE("Computer vs Computer Match in Progress...")
    ;

    private String value;

    MenuText(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
