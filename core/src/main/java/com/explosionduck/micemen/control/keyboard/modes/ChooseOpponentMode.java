package com.explosionduck.micemen.control.keyboard.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.menu.ChangeModeCommand;
import com.explosionduck.micemen.command.menu.InitialTurnCommand;
import com.explosionduck.micemen.command.menu.SetOpponentCommand;
import com.explosionduck.micemen.domain.OpponentMode;
import com.explosionduck.micemen.fx.MenuText;

import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.emptySet;

public class ChooseOpponentMode implements ControlMode {

    @Override
    public Collection<Command> generateCommands() {
        OpponentMode opponentMode = null;

        if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            opponentMode = OpponentMode.HUMAN_VS_HUMAN;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            opponentMode = OpponentMode.HUMAN_VS_CPU;
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            opponentMode = OpponentMode.CPU_VS_CPU;
//TODO            setControllers(new ComputerPlayerBasic(pool, scorer, grid, Team.RED),
//                    new ComputerPlayerMid2(pool, scorer, grid, Team.BLUE, 3));
        }

        if (opponentMode != null) {
            var commands = new ArrayList<Command>();
            commands.add(new SetOpponentCommand(opponentMode));
            commands.add(switch (opponentMode) {
                case HUMAN_VS_HUMAN -> new ChangeModeCommand(ControlModeType.GAME, MenuText.STANDARD_OPTIONS);
                case HUMAN_VS_CPU -> new ChangeModeCommand(ControlModeType.CHOOSE_DIFFICULTY, MenuText.CHOOSE_DIFFICULTY);
                case CPU_VS_CPU -> new ChangeModeCommand(ControlModeType.GAME, MenuText.CPU_BATTLE);
            });
            commands.add(new InitialTurnCommand(null));
            return commands;
        }

        return emptySet();
    }

    @Override
    public ControlModeType getType() {
        return ControlModeType.CHOOSE_OPPONENT;
    }
}
