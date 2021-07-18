package com.explosionduck.micemen.control.keyboard.modes;

import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.menu.LoadGameCommand;
import com.explosionduck.micemen.util.GameRestarter;

import java.util.Collection;
import java.util.concurrent.ExecutorService;

import static java.util.Collections.singleton;

public class ConfirmLoadMode extends AbstractConfirmationMode {

    private final GameRestarter restart;
    private final BoardScorer boardScorer;
    private final ExecutorService executorService;
    private final GridCalculator gridCalculator;

    public ConfirmLoadMode(
            GameRestarter restart,
            BoardScorer boardScorer,
            ExecutorService executorService,
            GridCalculator gridCalculator) {
        this.restart = restart;
        this.boardScorer = boardScorer;
        this.executorService = executorService;
        this.gridCalculator = gridCalculator;
    }

    @Override
    protected Collection<Command> getCommandsForAffirmative() {
        return singleton(new LoadGameCommand(restart, boardScorer, executorService, gridCalculator));
    }

    @Override
    public ControlModeType getType() {
        return ControlModeType.CONFIRM_LOAD;
    }
}
