package com.explosionduck.micemen.control.keyboard.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.menu.ChangeModeCommand;
import com.explosionduck.micemen.command.menu.InitialTurnCommand;
import com.explosionduck.micemen.command.menu.SetDifficultyCommand;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.Difficulty;
import com.explosionduck.micemen.fx.MenuText;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.emptySet;

public class ChooseDifficultyMode implements ControlMode {

    private final ExecutorService executorService;
    private final GridCalculator calculator;
    private final CheeseGrid grid;

    public ChooseDifficultyMode(ExecutorService executorService, GridCalculator calculator, CheeseGrid grid) {
        this.executorService = executorService;
        this.calculator = calculator;
        this.grid = grid;
    }

    @Override
    public Collection<Command> generateCommands() {
        Difficulty difficulty = null;
        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            difficulty = Difficulty.HARD;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.M)) {
            difficulty = Difficulty.MEDIUM;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            difficulty = Difficulty.EASY;
        }

        if (difficulty != null) {
            var commands = new ArrayList<Command>();
            commands.add(new SetDifficultyCommand(executorService, calculator, grid, difficulty));
            commands.add(new ChangeModeCommand(ControlModeType.GAME, MenuText.STANDARD_OPTIONS));
            commands.add(new InitialTurnCommand(null));
            return commands;
        }

        return emptySet();
    }

    @Override
    public ControlModeType getType() {
        return ControlModeType.CHOOSE_DIFFICULTY;
    }
}
