package com.explosionduck.micemen.command;

import com.explosionduck.micemen.domain.GameContext;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public class DefaultCommandExecutor implements CommandExecutor {

    private final Queue<Command> commands;

    public DefaultCommandExecutor() {
        this.commands = new LinkedList<>();
    }

    @Override
    public void addCommand(Command command) {
        this.commands.add(command);
    }

    @Override
    public void addCommands(Collection<Command> commands) {
        this.commands.addAll(commands);
    }

    @Override
    public boolean hasCommands() {
        return !this.commands.isEmpty();
    }

    @Override
    public void executeNextCommand(GameContext gameContext) {
        Command command = this.commands.peek(); // TODO: may need to use a different access method
        Collection<Command> newCommands = Collections.emptySet();
        if (command != null) {
            newCommands = command.execute(gameContext);
            if (command.isComplete()) {
                this.commands.poll();
            }
        }
        this.addCommands(newCommands);
    }

    @Override
    public void executeAll(GameContext gameContext) {
        while (this.commands.size() > 0) {
            this.executeNextCommand(gameContext);
        }
    }
}
