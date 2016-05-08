package gridparts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import control.BlockIter;
import control.ComputerPlayer;
import control.Direction;
import model.Block;
import model.CheeseBlock;
import model.CheeseException;
import model.CheeseGrid;
import model.EmptyBlock;
import model.Mouse;
import model.Mouse.Team;
import model.SimPoint;
import orders.Combo;
import orders.IOrder;
import orders.MouseMove;
import orders.MuscleFlexDrop;
import util.NonNullList;

public class GridController {
    
    private CheeseGrid                               grid;
    private List<IOrder>                             orders;
    
    private static final Map<Integer, List<Boolean>> PLACEMENT = new HashMap<>();
    static {
        for (Integer x : Arrays.asList(1, 5, 9, 11, 15, 19))
            PLACEMENT.put(x, Arrays.asList(true, false, false, true, false, false, true, false, false, true, false,
                    false, true));
        List<Boolean> empty = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false,
                false, false);
        PLACEMENT.put(0, empty);
        PLACEMENT.put(10,
                Arrays.asList(false, true, true, false, true, true, false, true, true, false, true, true, false));
        PLACEMENT.put(20, empty);
    }
    private static final List<Integer> VERBOTEN    = Arrays.asList(0, 1, 8, 9, 10, 11, 12, 19, 20);
    private static final int           CHEESE_WALL = 7;
    
    public static class Scores {
        public int red;
        public int blue;
    }
    
    private static final List<Integer> xs = IntStream.range(0, 21).boxed().collect(Collectors.toList());
    private static final List<Integer> ys = IntStream.range(0, 13).boxed().collect(Collectors.toList());
    
    public GridController(CheeseGrid grid) {
        this.grid = grid;
        this.orders = new ArrayList<>();
    }
    
    public List<IOrder> orders() {
        return orders;
    }
    
    public void fillGrid() throws CheeseException {
        int redCount = 0;
        int blueCount = 0;
        
        Collections.shuffle(xs);
        Collections.shuffle(ys);
        
        for (Integer x : xs) {
            for (Integer y : ys) {
                Block result = new EmptyBlock(grid);
                
                if (PLACEMENT.containsKey(x)) {
                    if (PLACEMENT.get(x).get(y))
                        result = new CheeseBlock(grid);
                } else if (columnCheeseCount(x) < CHEESE_WALL && Math.random() > .45) {
                    result = new CheeseBlock(grid);
                } else {
                    boolean placeMouse = Math.random() > .4;
                    if (placeMouse && !VERBOTEN.contains(x)) {
                        if (x < 8 && redCount < grid.micePerTeam() && columnMouseCount(x, Team.RED) < 2
                                && rowMouseCount(y, Team.RED) == 0) {
                            result = new Mouse(Team.RED, grid);
                            redCount += 1;
                        } else if (x > 12 && blueCount < grid.micePerTeam() && columnMouseCount(x, Team.BLUE) < 2
                                && rowMouseCount(y, Team.BLUE) == 0) {
                            result = new Mouse(Team.BLUE, grid);
                            blueCount += 1;
                        }
                    }
                }
                
                grid.set(x, y, result);
                if (grid.get(x, y) == null)
                    throw new CheeseException("Null block detected");
            }
        }
        
        /** do 2nd pass to ensure enough mice have been placed */
        secondPass(grid.micePerTeam() - redCount, Team.RED);
        secondPass(grid.micePerTeam() - blueCount, Team.BLUE);
    }
    
    private void secondPass(int difference, Team team) throws CheeseException {
        int start;
        Direction dir;
        if (team == Team.RED) {
            start = 7;
            dir = Direction.LEFT;
        } else {
            start = 13;
            dir = Direction.RIGHT;
        }
        
        BlockIter<EmptyBlock> iter = new BlockIter<>(grid, Direction.UP, dir, start, grid.hMax(), EmptyBlock.class)
                .xLimit(5).yLimit(0).max(difference);
        
        List<EmptyBlock> emptyBlocks = new ArrayList<>();
        iter.forEachRemaining(e -> emptyBlocks.add(e));
        Collections.shuffle(emptyBlocks);
        
        for (int i = 0; i < difference; i++) {
            Block empty = emptyBlocks.get(i);
            SimPoint esp = grid.get(empty);
            
            grid.fillVacancy(esp.x(), esp.y(), team);
        }
    }
    
    public Scores valueBoard(boolean returnScores) {
        int red = ComputerPlayer.measureGridValue(grid, Team.RED);
        int blue = ComputerPlayer.measureGridValue(grid, Team.BLUE);
        
        if (red > blue) {
            grid.state().menu().boardFavor(Team.RED, red - blue);
        } else if (blue > red) {
            grid.state().menu().boardFavor(Team.BLUE, blue - red);
        } else {
            grid.state().menu().message = "Fair Game";
        }
        
        if (returnScores) {
            Scores scores = new Scores();
            scores.red = red;
            scores.blue = blue;
            return scores;
        } else
            return null;
    }
    
    private int columnCheeseCount(int x) {
        int count = 0;
        for (int y = 0; y < grid.height(); y++) {
            Block block = grid.get(x, y);
            if (block != null && block.isCheese())
                count++;
        }
        return count;
    }
    
    public int columnMouseCount(int x, Team team) {
        int count = 0;
        for (int y = 0; y < grid.height(); y++) {
            Block block = grid.get(x, y);
            if (block != null && block.isMouse() && (team == null || block.isTeam(team)))
                count++;
        }
        return count;
    }
    
    private int rowMouseCount(int y, Team team) {
        int count = 0;
        for (int x = 0; x < grid.width(); x++) {
            Block block = grid.get(x, y);
            if (block != null && block.isMouse() && (team == null || block.isTeam(team)))
                count++;
        }
        return count;
    }
    
    public int score(Team team) {
        int miceLeft = 0;
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                if (block.isMouse() && block.isTeam(team)) {
                    miceLeft++;
                }
            }
        }
        return grid.micePerTeam() - miceLeft;
    }
    
    public Scores scores() {
        Scores result = new Scores();
        int redsLeft = 0;
        int bluesLeft = 0;
        
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                if (block.isRedMouse())
                    redsLeft++;
                else if (block.isBlueMouse())
                    bluesLeft++;
            }
        }
        
        result.red = grid.micePerTeam() - redsLeft;
        result.blue = grid.micePerTeam() - bluesLeft;
        
        return result;
    }
    
    public boolean poleIsAvailable(int x) {
        boolean poleIsBlocked = grid.X() != null && x == grid.X();
        return !poleIsBlocked && grid.poles()[x];
    }
    
    public void recalculateMoves() throws CheeseException {
        CheeseGrid copygrid = new CheeseGrid(grid);
        Team lastTeam = grid.activeTeam() == Team.RED ? Team.RED : Team.BLUE;
        
        List<IOrder> newOrders = recalc(copygrid, lastTeam);
        
        orders.addAll(newOrders);
    }
    
    private static List<IOrder> recalc(CheeseGrid copygrid, Team lastTeam) throws CheeseException {
        final List<IOrder> results = new NonNullList<>();
        final boolean red = lastTeam == Team.BLUE;
        final Direction activeDir = red ? Direction.LEFT : Direction.RIGHT, //
                inactiveDir = !red ? Direction.LEFT : Direction.RIGHT;
        final int activeStart = red ? copygrid.wMax() : 0, //
                inActiveStart = !red ? copygrid.wMax() : 0;
        final Team otherTeam = lastTeam == Team.RED ? Team.BLUE : Team.RED;
        
        while (true) {
            
            /** first, look for falls */
            Iterator<Mouse> allMice = new BlockIter<>(copygrid, Direction.UP, activeDir, activeStart, copygrid.hMax(),
                    Mouse.class);
            IOrder move = findFirstMove(copygrid, allMice, true);
            if (results.add(move))
                continue;
            
            /** the active team then moves */
            Iterator<Mouse> activeTeam = allMice = new BlockIter<>(copygrid, Direction.UP, activeDir, activeStart,
                    copygrid.hMax(), Mouse.class).team(lastTeam);
            move = findFirstMove(copygrid, activeTeam, false);
            if (results.add(move))
                continue;
            
            /** non-active team last */
            Iterator<Mouse> nonactiveTeam = new BlockIter<>(copygrid, Direction.UP, inactiveDir, inActiveStart,
                    copygrid.hMax(), Mouse.class).team(otherTeam);
            move = findFirstMove(copygrid, nonactiveTeam, false);
            if (results.add(move))
                continue;
            
            /** if no moves found, stop */
            break;
        }
        
        return results;
    }
    
    private static IOrder findFirstMove(CheeseGrid copygrid, Iterator<Mouse> mice, boolean fallsOnly)
            throws CheeseException {
        // TODO: mutating the copygrid as part of the move-getting process is
        // kind of sloppy
        // -- better to create full MouseMoves and MuscleFlexDrops and apply
        // them on the spot
        
        while (mice.hasNext()) {
            // for (Mouse mouse : mice) {
            Mouse mouse = mice.next();
            MouseMove move = mouse.getMoves(copygrid, fallsOnly);
            SimPoint total = move.consolidate();
            int totalX = Math.abs(total.x());
            int totalY = Math.abs(total.y());
            
            if (fallsOnly) {
                if (totalX == 0 && totalY > 0)
                    return move;
            } else {
                if (totalX + totalY > 0) {
                    MuscleFlexDrop muscle = copygrid.ctrl().removeEscapedMice();
                    if (muscle != null) {
                        Combo combo = new Combo().add(move).add(muscle);
                        
                        /** must also immediately update copygrid */
                        muscle.executeOnCopygrid(copygrid);
                        
                        return combo;
                    }
                    return move;
                }
            }
        }
        return null;
    }
    
    public List<Block> columnCopy(int x) {
        List<Block> result = new ArrayList<>();
        for (int y = 0; y < grid.height(); y++)
            result.add(grid.get(x, y));
        return result;
    }
    
    public MuscleFlexDrop removeEscapedMice() {
        Mouse mouse;
        int x = -1;
        Block left = grid.get(0, grid.hMax());
        Block right = grid.get(grid.wMax(), grid.hMax());
        if (left.isMouse()) {
            mouse = (Mouse) left;
            x = 0;
        } else if (right.isMouse()) {
            mouse = (Mouse) right;
            x = grid.wMax();
        } else {
            return null;
        }
        
        return new MuscleFlexDrop(mouse, x);
    }
    
    public void executeAll() throws CheeseException {
        while (orders.size() > 0)
            executeNext();
    }
    
    public void executeNext() throws CheeseException {
        IOrder order = orders.get(0);
        order.execute(grid);
        if (order.finished())
            orders.remove(order);
    }
    
}
