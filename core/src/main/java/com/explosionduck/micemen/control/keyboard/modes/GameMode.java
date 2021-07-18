package com.explosionduck.micemen.control.keyboard.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.command.grid.SetHandCommand;
import com.explosionduck.micemen.command.menu.ChangeModeCommand;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.Direction;
import com.explosionduck.micemen.fx.MenuText;

import java.util.Collection;

import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;

public class GameMode implements ControlMode {

    private final GridCalculator calculator;
    private final CheeseGrid grid;

    public GameMode(GridCalculator calculator, CheeseGrid grid) {
        this.calculator = calculator;
        this.grid = grid;
    }

    @Override
    public Collection<Command> generateCommands() {
        int selectedPole = grid.getActivePole();

        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            return singleton(new ColumnShiftCommand(calculator, selectedPole, Direction.UP));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            return singleton(new ColumnShiftCommand(calculator, selectedPole, Direction.DOWN));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            if (selectedPole - 1 >= 0)
                return singleton(new SetHandCommand(grid, -1, true));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            if (selectedPole - 1 <= grid.getWidthMax())
                return singleton(new SetHandCommand(grid, 1, true));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) { // USER PRESSED A MENU KEY
            return singleton(new ChangeModeCommand(ControlModeType.CONFIRM_NEW_GAME, MenuText.CONFIRM_NEW_GAME));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            return singleton(new ChangeModeCommand(ControlModeType.CONFIRM_SAVE, MenuText.CONFIRM_SAVE));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
            return singleton(new ChangeModeCommand(ControlModeType.CONFIRM_LOAD, MenuText.CONFIRM_LOAD));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
            return singleton(new ChangeModeCommand(ControlModeType.CONFIRM_QUIT, MenuText.CONFIRM_QUIT));
        }

        return emptySet();
    }

    @Override
    public ControlModeType getType() {
        return ControlModeType.GAME;
    }
}
