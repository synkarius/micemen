package com.explosionduck.micemen.command.grid;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.CommandType;
import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;
import java.util.Collections;

public class ProgressCommand implements Command {

    private double ready;
    /** total=0 indicates reset the menu state */
    private double total;

    /** draws a progress bar on the screen */
    public ProgressCommand(double ready, double total) {
        this.ready = ready;
        this.total = total;
    }

    @Override
    public Collection<Command> execute(GameContext gameContext) {
        gameContext.setThinkingReady(ready);
        gameContext.setThinkingTotal(total);
        return Collections.emptySet();
    }

    @Override
    public CommandType getType() {
        return CommandType.PROGRESS;
    }
}