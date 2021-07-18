package com.explosionduck.micemen.calculation.calculator;

import com.explosionduck.micemen.command.Command;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;

import java.util.Collection;

public interface GridCalculator {

    /**
     * Generates a list of moves for the mice on the grid, filling empty spaces, and moving forward as allowed.
     *
     * @param grid
     * @return
     * @throws CheeseException
     */
    Collection<Command> calculateNewCommands(CheeseGrid grid) throws CheeseException;

    /**
     * Generates an array of possible column choices for a given grid.
     *
     * @param grid
     * @return
     */
    ColumnShiftCommand[] getChoices(CheeseGrid grid);
}
