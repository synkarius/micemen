package file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import control.ComputerPlayerBasic;
import control.ComputerPlayerMid2;
import control.IController;
import control.KeyboardController;
import model.Block;
import model.CheeseBlock;
import model.CheeseException;
import model.CheeseGrid;
import model.EmptyBlock;
import model.Mouse;
import model.Mouse.Team;

public class Board {
    private static final Logger log = Logger.getLogger(Board.class.getName());
    
    private static Path savePath() {
        return Paths.get("mice.txt").toAbsolutePath();
    }
    
    public static String timestamp() {
        return new Date().toString().replace(':', ' ');
    }
    
    private static Path oldSavePath() {
        return Paths.get(timestamp() + ".oldsave").toAbsolutePath();
    }
    
    public static String getBoardString(CheeseGrid grid) {
        return getBoardString(grid, false);
    }
    
    public static String getBoardString(CheeseGrid grid, boolean pretty) {
        StringBuilder board = new StringBuilder();
        
        for (int y = 0; y < grid.height(); y++) {
            if (pretty)
                board.append(y % 10).append(" ");
            for (int x = 0; x < grid.width(); x++) {
                Block block = grid.get(x, y);
                switch (block.type()) {
                    case MOUSE:
                        Mouse mouse = (Mouse) block;
                        board.append(mouse.team() == Team.RED ? "r" : "b");
                        break;
                    case CHEESE:
                        board.append("#");
                        break;
                    case EMPTY:
                        board.append(".");
                        break;
                }
                if (pretty)
                    board.append(" ");
            }
            board.append("\n");
        }
        if (pretty)
            board.append("  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0");
        
        return board.toString();
    }
    
    public static void saveGame(CheeseGrid grid, IController blue) {
        String board = getBoardString(grid);
        String opponentWasCPU = (blue == null || !(blue instanceof KeyboardController)) ? "1" : "0";
        String team = grid.activeTeam() == Team.RED ? "0" : "1";
        String level = (blue instanceof ComputerPlayerMid2) ? ((ComputerPlayerMid2) blue).lookAhead() + "" : "0";
        
        if (Files.exists(savePath()))
            try {
                Files.move(savePath(), oldSavePath());
            } catch (IOException e1) {
                log.log(Level.SEVERE, "Save failure (1)", e1);
            }
        
        try (BufferedWriter writer = Files.newBufferedWriter(savePath())) {
            
            writer.write(level);// reserved for CPU level
            writer.write(opponentWasCPU);
            writer.write(team);
            writer.newLine();
            writer.write(board);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Save failure (2)", e);
        }
    }
    
    public static CheeseGrid loadFromSave() {
        CheeseGrid result = CheeseGrid.getNewDefault();
        
        try (BufferedReader reader = Files.newBufferedReader(savePath())) {
            
            int x = 0;
            int y = 0;
            
            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().isEmpty())
                    continue;
                
                /** if this is a config line (the first line) */
                if (!line.startsWith(".")) {
                    result.opponentLevel(Integer.parseInt(line.substring(0, 1)));
                    result.opponentWasCPU(line.charAt(1) == '1');
                    result.activeTeam((line.charAt(2) == '1') ? Team.BLUE : Team.RED);
                    continue;
                }
                
                String[] cells = line.split("");
                for (String cell : cells) {
                    Block block;
                    switch (cell) {
                        case "r":
                            block = new Mouse(Team.RED, result);
                            break;
                        case "b":
                            block = new Mouse(Team.BLUE, result);
                            break;
                        case "#":
                            block = new CheeseBlock(result);
                            break;
                        case ".":
                            block = new EmptyBlock(result);
                            break;
                        default:
                            continue;
                    }
                    
                    result.set(x, y, block);
                    if (result.get(x, y) == null)
                        throw new CheeseException("Null block detected");
                    
                    if (x + 1 < result.width()) {
                        x++;
                    } else {
                        x = 0;
                        y++;
                    }
                }
            }
        } catch (IOException | CheeseException e) {
            log.log(Level.SEVERE, "Load failure", e);
            return null;
        }
        
        return result;
    }
    
}
