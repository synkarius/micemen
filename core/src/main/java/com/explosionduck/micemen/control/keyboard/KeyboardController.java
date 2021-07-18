package com.explosionduck.micemen.control.keyboard;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.control.Controller;
import com.explosionduck.micemen.control.ControllerType;
import com.explosionduck.micemen.control.keyboard.modes.ControlMode;
import com.explosionduck.micemen.control.keyboard.modes.ControlModeType;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;
import java.util.Map;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

public class KeyboardController implements Controller {

    private final Map<ControlModeType, ControlMode> modes;
    private final GameContext context;

    public KeyboardController(GameContext context, Collection<ControlMode> modes) {
        this.modes = modes.stream().collect(toMap(ControlMode::getType, identity()));
        this.context = context;
    }

    @Override
    public Collection<Command> getCommands() throws CheeseException {
        var activeMode = this.modes.get(this.context.getControlModeType());
        return activeMode.generateCommands();
    }

    @Override
    public ControllerType getControllerType() {
        return ControllerType.HUMAN;
    }
}
