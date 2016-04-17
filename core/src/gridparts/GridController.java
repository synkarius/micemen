package gridparts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import control.BlockIter;
import control.Direction;
import model.Block;
import model.CheeseBlock;
import model.CheeseException;
import model.CheeseGrid;
import model.EmptyBlock;
import model.Mouse;
import model.SimPoint;
import model.Block.Type;
import model.Mouse.Team;
import orders.IOrder;
import orders.MouseMove;
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
        
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
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
        
        BlockIter iter = new BlockIter(grid, Arrays.asList(Direction.UP, dir), start, grid.hMax()).xLimit(5)
                .yLimit(0)
                .type(Type.EMPTY)
                // .side(team)
                .listLimit(difference);
        
        List<Block> emptyBlocks = iter.toList();
        Collections.shuffle(emptyBlocks);
        
        for (int i = 0; i < difference; i++) {
            Block empty = emptyBlocks.get(i);
            SimPoint esp = grid.get(empty);
            
            grid.fillVacancy(esp.x(), esp.y(), team);
        }
    }
    
    private String print() {
        return print(false);
    }
    
    private String print(boolean pretty) {
        StringBuilder printed = new StringBuilder();
        for (int y = 0; y < grid.height(); y++) {
            for (int x = 0; x < grid.width(); x++) {
                switch (grid.get(x, y).type()) {
                    case CHEESE:
                        printed.append('#');
                        break;
                    case EMPTY:
                        printed.append('.');
                        break;
                    case MOUSE:
                        printed.append(grid.get(x, y).isRedMouse() ? 'r' : 'b');
                        break;
                }
                printed.append(' ');
            }
            printed.append('\n');
        }
        return printed.toString();
    }
    
    private void valueBoard() {
        // TODO
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
    
    public boolean poleIsAvailable(int x) {
        boolean poleIsBlocked = grid.X() != null && x == grid.activePole();
        return !poleIsBlocked && grid.poles()[x];
    }
    
    public void recalculateMoves() throws CheeseException {
        CheeseGrid copygrid = new CheeseGrid(grid);
        Team newActiveTeam = grid.activeTeam() == Team.RED ? Team.BLUE : Team.RED;
        
        List<IOrder> newOrders = recalc(copygrid, newActiveTeam);
        
        orders.addAll(newOrders);
    }
    
    private static List<IOrder> recalc(CheeseGrid copygrid, Team newActiveTeam) throws CheeseException {
        final List<IOrder> results = new NonNullList<>();
        final boolean red = newActiveTeam == Team.RED;
        final Direction activeDir = red ? Direction.LEFT : Direction.RIGHT, //
                inactiveDir = !red ? Direction.LEFT : Direction.RIGHT;
        final int activeStart = red ? copygrid.wMax() : 0, //
                inActiveStart = !red ? copygrid.wMax() : 0;
        
        while (true) {
            
            /** first, look for falls */
            List<Mouse> allMice = copygrid.ctrl().getAllMiceWithDirection(Direction.UP, activeDir, activeStart,
                    copygrid.hMax());
            MouseMove move = findFirstMove(copygrid, allMice, true);
            if (results.add(move))
                continue;
            
            /** the active team then moves */
            List<Mouse> activeTeam = allMice.stream()
                    .filter(mouse -> active(mouse, true, newActiveTeam))
                    .collect(Collectors.toList());
            move = findFirstMove(copygrid, activeTeam, false);
            if (results.add(move))
                continue;
            
            /** non-active team last */
            List<Mouse> nonactiveTeam = copygrid.ctrl()
                    .getAllMiceWithDirection(Direction.UP, inactiveDir, inActiveStart, copygrid.hMax())
                    .stream()
                    .filter(mouse -> active(mouse, false, newActiveTeam))
                    .collect(Collectors.toList());
            move = findFirstMove(copygrid, nonactiveTeam, false);
            if (results.add(move))
                continue;
            
            /** if no moves found, stop */
            break;
        }
        
        return results;
    }
    
    private static MouseMove findFirstMove(CheeseGrid copygrid, List<Mouse> mice, boolean fallsOnly)
            throws CheeseException {
        for (Mouse mouse : mice) {
            MouseMove move = mouse.getMoves(copygrid, fallsOnly);
            SimPoint total = move.consolidate();
            int totalX = Math.abs(total.x());
            int totalY = Math.abs(total.y());
            
            if (fallsOnly) {
                if (totalX == 0 && totalY > 0) {
                    return move;
                }
            } else {
                if (totalX + totalY > 0)
                    return move;
            }
        }
        return null;
    }
    
    private List<Mouse> getAllMice() {
        return getAllMiceWithDirection(Direction.DOWN, Direction.RIGHT, 0, 0);
    }
    
    private List<Mouse> getAllMiceWithDirection(Direction first, Direction second, int startX, int startY) {
        return new BlockIter(grid, Arrays.asList(first, second), startX, startY).type(Type.MOUSE)
                .toList()
                //
                .stream()
                .map(block -> (Mouse) block)
                .collect(Collectors.toList());
    }
    
    private static List<Mouse> filterForTeam(List<Mouse> mice, Team team) {
        return mice.stream().filter(mouse -> mouse.isTeam(team)).collect(Collectors.toList());
    }
    
    public List<Block> columnCopy(int x){
        List<Block> result = new ArrayList<>();
        for (int y=0; y < grid.height(); y++)
            result.add(grid.get(x, y));
        return result;
    }
    
    private static boolean active(Mouse block, boolean getUp, Team team) {
        boolean result = (team == Team.RED && block.isRedMouse()) || (team == Team.BLUE && block.isBlueMouse());
        if (getUp)
            return result;
        else
            return !result;
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
