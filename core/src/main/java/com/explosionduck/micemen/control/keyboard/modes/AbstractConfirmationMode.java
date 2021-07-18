package com.explosionduck.micemen.control.keyboard.modes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.menu.Resumer;

import java.util.Collection;

import static java.util.Collections.emptySet;

/**
 * If the user declines, {@link AbstractConfirmationMode} signals to return to game mode.
 */
public abstract class AbstractConfirmationMode implements ControlMode, Resumer {

    @Override
    public Collection<Command> generateCommands() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.Y)) {
            return this.getCommandsForAffirmative();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.N)) {
            return this.getCommandsToReturnToGameMode();
        } else {
            return emptySet();
        }
    }

    protected abstract Collection<Command> getCommandsForAffirmative();
}
