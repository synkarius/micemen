package simulate;

import java.util.stream.IntStream;

import control.ComputerPlayerBasic;
import control.IController;
import file.Board;
import gridparts.GridController.Scores;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.IOrder;
import orders.PassTurn;

public class Simulator {
    public static void simulate() throws CheeseException {
        
        long totalStart = java.lang.System.currentTimeMillis();
        int redWins = 0;
        int blueWins = 0;
        int redStartedAhead = 0;
        int blueStartedAhead = 0;
        int redWentFirst = 0;
        int blueWentFirst = 0;
        
        allGames: for (int i = 0; i < 1000; i++) {
            CheeseGrid grid = Board.loadFromSave();
            // CheeseGrid.getNewDefault();
            // grid.activeTeam(Team.RED);
            // grid.ctrl().fillGrid();
            grid.ctrl().recalculateMoves();
            grid.ctrl().executeAll();
            
            Scores starting = grid.ctrl().valueBoard(true);
            if (starting.red > starting.blue) {
                redStartedAhead++;
            } else if (starting.blue > starting.red) {
                blueStartedAhead++;
            }
            
            IController redController = new ComputerPlayerBasic().grid(grid).team(Team.RED);
            IController blueController = new ComputerPlayerBasic().grid(grid).team(Team.BLUE);
            int redScore = 0;
            int blueScore = 0;
            int moveCount = 0;
            
            grid.ctrl().orders().add(new PassTurn());
            if (Math.random() < .5)
                grid.ctrl().orders().add(new PassTurn());
            grid.ctrl().executeAll();
            
            long start = java.lang.System.currentTimeMillis();
            
            thisGame: while (true) {
                IController controller = grid.activeTeam() == Team.RED ? redController : blueController;
                IOrder order = controller.getOrder();
                moveCount++;
                
                grid.ctrl().orders().add(order);
                grid.ctrl().executeAll();
                
                Scores scores = grid.ctrl().scores();
                redScore = scores.red;
                blueScore = scores.blue;
                
                if (moveCount > 200) {
                    continue allGames;
                }
                
                if (redScore == grid.micePerTeam() || blueScore == grid.micePerTeam())
                    break;
            }
            
            long delta = java.lang.System.currentTimeMillis() - start;
            
            java.lang.System.out.println("game: " + i + " moves: " + moveCount + " ; red: " + redScore + " ; blue: "
                    + blueScore + " ; time(ms): " + delta);
            
            if (redScore == grid.micePerTeam() || blueScore == grid.micePerTeam())
                if (redScore > blueScore)
                    redWins++;
                else if (blueScore > redScore)
                    blueWins++;
            
        }
        
        java.lang.System.out.println("total time: " + (java.lang.System.currentTimeMillis() - totalStart) + //
                " red: " + redWins + " blue: " + blueWins + //
                " headstarts:: red: " + redStartedAhead + " blue: " + blueStartedAhead);
        
    }
}
