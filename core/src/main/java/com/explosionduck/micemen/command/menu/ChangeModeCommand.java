package com.explosionduck.micemen.command.menu;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.control.keyboard.modes.ControlModeType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;
import com.explosionduck.micemen.fx.LeftSideMessage;

import java.util.Collection;

import static java.util.Collections.emptySet;

public class ChangeModeCommand implements Command {

    private final ControlModeType controlModeType;
    private final String menuMessage;
    private final LeftSideMessage menuText;

    public ChangeModeCommand(ControlModeType controlModeType, String menuMessage) {
        this.controlModeType = controlModeType;
        this.menuMessage = menuMessage;
        this.menuText = null;
    }

    public ChangeModeCommand(ControlModeType controlModeType, LeftSideMessage menuText) {
        this.controlModeType = controlModeType;
        this.menuMessage = null;
        this.menuText = menuText;
    }

    @Override
    public Collection<Command> execute(GameContext context) throws CheeseException {
        context.setControlModeType(this.controlModeType);
        if (this.menuMessage != null) {
            context.setMessage(this.menuMessage);
        }
        if (this.menuText != null) {
            context.setText(this.menuText.getValue());
        }
        return emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.CHANGE_MODE;
    }
}
