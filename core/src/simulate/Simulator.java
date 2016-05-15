package simulate;

import java.util.concurrent.ExecutorService;

import control.ComputerPlayer;
import control.ComputerPlayerBasic;
import control.ComputerPlayerMid2;
import control.IController;
import file.Board;
import gridparts.GridController;
import gridparts.GridController.Scores;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.IOrder;
import orders.OrderType;
import orders.PassTurn;

public class Simulator {
    public static void simulate(ExecutorService pool) throws CheeseException {
        
        boolean playSingle = false;
        int games = playSingle ? 1 : 1000;
        
        long totalStart = java.lang.System.currentTimeMillis();
        int redWins = 0;
        int blueWins = 0;
        int redStartedAhead = 0;
        int blueStartedAhead = 0;
        
        allGames: for (int i = 0; i < games; i++) {
            
            CheeseGrid grid;
            if (playSingle) {
                grid = Board.loadFromSave();
            } else {
                grid = CheeseGrid.getNewDefault();
                grid.activeTeam(Team.RED);
                grid.ctrl().fillGrid();
            }
            grid.ctrl().recalculateMoves();
            grid.ctrl().executeAll();
            
            grid.recording().startRecording();
            
            Scores starting = GridController.scores(grid, true);
            if (starting.redBoardValue > starting.blueBoardValue) {
                redStartedAhead++;
            } else if (starting.blueBoardValue > starting.redBoardValue) {
                blueStartedAhead++;
            }
            
            ComputerPlayer redController = new ComputerPlayerBasic(pool).grid(grid).team(Team.RED);
            ComputerPlayer blueController = new ComputerPlayerMid2(pool).grid(grid).team(Team.BLUE);
            int redScore = 0;
            int blueScore = 0;
            int moveCount = 0;
            
            grid.ctrl().orders().add(new PassTurn());
            if (playSingle || Math.random() < .5)
                grid.ctrl().orders().add(new PassTurn());
            grid.ctrl().executeAll();
            
            long start = java.lang.System.currentTimeMillis();
            
            while (true) {
                IController controller = grid.activeTeam() == Team.RED ? redController : blueController;
                IOrder order = controller.getOrder();
                
                grid.ctrl().orders().add(order);
                grid.ctrl().executeAll();
                
                if (order.type() != OrderType.PROGRESS) {
                    Scores scores = GridController.scores(grid, false);
                    redScore = scores.redScore;
                    blueScore = scores.blueScore;
                    
                    if (++moveCount > 200) {
                        continue allGames;
                    }
                    
                    if (redScore == grid.micePerTeam() || blueScore == grid.micePerTeam())
                        break;
                }
            }
            
            long delta = java.lang.System.currentTimeMillis() - start;
            
            if (playSingle)
                java.lang.System.out.println(grid.recording().toString());
            
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
