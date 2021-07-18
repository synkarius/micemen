package com.explosionduck.micemen.io;

import com.explosionduck.micemen.control.Controller;
import com.explosionduck.micemen.control.ControllerType;
import com.explosionduck.micemen.control.computer.ComputerPlayer;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.*;

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

public class FileIO {

    private static final Logger log = Logger.getLogger(FileIO.class.getName());

    private static Path savePath() {
        return Paths.get("mice.txt").toAbsolutePath();
    }

    public static String timestamp() {
        return new Date().toString().replace(':', ' ');
    }

    private static Path oldSavePath() {
        return Paths.get(timestamp() + ".oldsave").toAbsolutePath();
    }

    public static String serializeGrid(CheeseGrid grid) {
        return serializeGrid(grid, false);
    }

    public static String serializeGrid(CheeseGrid grid, boolean pretty) {
        var board = new StringBuilder();

        for (int y = 0; y < grid.getHeight(); y++) {
            if (pretty) {
                board.append(y % 10)
                        .append(" ");
            }
            for (int x = 0; x < grid.getWidth(); x++) {
                var block = grid.getBlockAt(x, y);
                switch (block.getBlockType()) {
                    case MOUSE -> {
                        Mouse mouse = (Mouse) block;
                        board.append(mouse.getTeam() == Team.RED ? "r" : "b");
                    }
                    case CHEESE -> board.append("#");
                    case EMPTY -> board.append(".");
                }
                if (pretty) {
                    board.append(" ");
                }
            }
            board.append("\n");
        }
        if (pretty) {
            board.append("  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0");
        }

        return board.toString();
    }

    public static void saveGame(CheeseGrid grid, Controller blue) {
        var board = serializeGrid(grid);
        var opponentWasCPU = ControllerType.COMPUTER == blue.getControllerType() ? "1" : "0";
        var team = grid.getActiveTeam() == Team.RED ? "0" : "1";
        var level = ControllerType.COMPUTER == blue.getControllerType()
                ? ((ComputerPlayer) blue).getLookAhead() + ""
                : "0";
        if (Files.exists(savePath())) {
            try {
                Files.move(savePath(), oldSavePath());
            } catch (IOException e1) {
                log.log(Level.SEVERE, "Save failure: could not rename old save.", e1);
            }
        }

        try (BufferedWriter writer = Files.newBufferedWriter(savePath())) {

            writer.write(level);// reserved for CPU level
            writer.write(opponentWasCPU);
            writer.write(team);
            writer.newLine();
            writer.write(board);
        } catch (IOException e) {
            log.log(Level.SEVERE, "Save failure: could not write new save.", e);
        }
    }

    public static CheeseGrid loadFromSave() {
        try (BufferedReader reader = Files.newBufferedReader(savePath())) {
            var result = CheeseGrid.getNewDefault();

            int x = 0;
            int y = 0;

            for (String line : reader.lines().collect(Collectors.toList())) {
                if (line.trim().isEmpty())
                    continue;

                /* if this is a config line (the first line) */
                if (!line.startsWith(".")) {
                    result.setOpponentLevel(Integer.parseInt(line.substring(0, 1)));
                    result.setOpponentWasCPU(line.charAt(1) == '1');
                    result.setActiveTeam((line.charAt(2) == '1') ? Team.BLUE : Team.RED);
                    continue;
                }

                String[] cells = line.split("");
                for (String cell : cells) {
                    result.setInitialBlock(x, y, switch (cell) {
                        case "r" -> new Mouse(Team.RED);
                        case "b" -> new Mouse(Team.BLUE);
                        case "#" -> new CheeseBlock();
                        case "." -> new EmptyBlock();
                        default -> throw new CheeseException("Unsupported block type: " + cell);
                    });

                    if (x + 1 < result.getWidth()) {
                        x++;
                    } else {
                        x = 0;
                        y++;
                    }
                }
            }
            return result;
        } catch (IOException | CheeseException e) {
            log.log(Level.SEVERE, "Load failure", e);
            return null;
        }
    }

}
