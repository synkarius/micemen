package com.explosionduck.micemen.calculation;

import com.explosionduck.micemen.calculation.noop.NoOpGameContext;
import com.explosionduck.micemen.command.CommandExecutor;
import com.explosionduck.micemen.command.DefaultCommandExecutor;
import com.explosionduck.micemen.command.grid.ColumnShiftCommand;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.Team;

public class Analysis {

    /**
     * Used by {@link com.explosionduck.micemen.control.computer.BasicComputerPlayer} and {@link SimulationFlatTree}.
     */
    public static void analyzeShift(SimulationNode node) throws CheeseException {
        // execute the shift
        CommandExecutor commandExecutor = new DefaultCommandExecutor();
        commandExecutor.addCommand(node.getColumnShiftCommand());
        commandExecutor.executeAll(new NoOpGameContext(node.getCopyGrid()));

        // score the board
        BoardScorer scorer = new BoardScorer(); // TODO: DI this
        BoardScorer.Scores eval = scorer.scores(node.getCopyGrid(), true);
        node.setValue(eval);
    }

    public static SimulationNode analyzeShift(
            ColumnShiftCommand columnShiftCommand, CheeseGrid grid, Team team, ColumnShiftCommand root)
            throws CheeseException {
        var node = new SimulationNode(columnShiftCommand, new CheeseGrid(grid), team, root);

        analyzeShift(node);

        return node;
    }
}
