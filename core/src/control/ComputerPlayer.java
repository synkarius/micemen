package control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import model.Block;
import model.CheeseException;
import model.CheeseGrid;
import model.Mouse.Team;
import orders.ColumnShift;
import simulate.SimulationNode;

public abstract class ComputerPlayer implements IController {
    
    public static final Comparator<ColumnShift> RED_SORT;
    public static final Comparator<ColumnShift> BLUE_SORT;
    static {
        RED_SORT = (a, b) -> {
            int xCompare = Integer.compare(a.x(), b.x());
            if (xCompare != 0)
                return xCompare;
            return a.dir().compareTo(b.dir());
        };
        BLUE_SORT = (a, b) -> {
            int xCompare = Integer.compare(b.x(), a.x());
            if (xCompare != 0)
                return xCompare;
            return a.dir().compareTo(b.dir());
        };
    }
    
    protected ExecutorService pool;
    protected CheeseGrid      grid;
    protected Team            team;
    
    public ComputerPlayer(ExecutorService pool) {
        this.pool = pool;
    }
    
    public ComputerPlayer grid(CheeseGrid grid) {
        this.grid = grid;
        return this;
    }
    
    public ComputerPlayer team(Team team) {
        this.team = team;
        return this;
    }
    
    public static List<ColumnShift> getChoices(CheeseGrid grid) {
        List<ColumnShift> choices = new ArrayList<>();
        
        for (int p = 0; p < grid.poles().length; p++) {
            if (grid.poles()[p] && grid.ctrl().poleIsAvailable(p)) {
                choices.add(new ColumnShift(p, Direction.UP));
                choices.add(new ColumnShift(p, Direction.DOWN));
            }
        }
        
        Comparator<ColumnShift> comparator = grid.activeTeam() == Team.RED ? RED_SORT : BLUE_SORT;
        Collections.sort(choices, comparator);
        
        return choices;
    }
    
    protected int gap() {
        int min = grid.wMax();
        int max = 0;
        for (int x = 0; x < grid.width(); x++) {
            for (int y = 0; y < grid.height(); y++) {
                Block block = grid.get(x, y);
                boolean countMouse = (block.isBlueMouse() && team == Team.BLUE)
                        || (block.isRedMouse() && team == Team.RED);
                if (countMouse) {
                    if (x > max)
                        max = x;
                    if (x < min)
                        x = min;
                }
            }
        }
        return Math.abs(max - min);
    }
    
    public static List<SimulationNode> splitAndBlock(ExecutorService pool, List<ColumnShift> choices, CheeseGrid grid,
            Team team) {
        List<Future<SimulationNode>> futures = new ArrayList<>();
        for (int c = 0; c < choices.size(); c++) {
            ColumnShift choice = choices.get(c);
            CheeseGrid copygrid = new CheeseGrid(grid);
            final int c1 = c;
            futures.add(pool.submit(new Callable<SimulationNode>() {
                
                @Override
                public SimulationNode call() throws Exception {
                    java.lang.System.out.println(" <> entering lambda for " + c1);
                    SimulationNode result = SimulationNode.analyzeShift(choice, copygrid, team);
                    java.lang.System.out.println(" <> exiting lambda for " + c1);
                    return result;
                }
            }));
        }
        
        List<SimulationNode> result = new ArrayList<>();
        try {
            for (int f = 0; f < futures.size(); f++) {
                java.lang.System.out.println(" ~ ~ ~ getting future " + f);
                Future<SimulationNode> future = futures.get(f);
                result.add(future.get());
                java.lang.System.out.println("~ ~ ~ got future " + f);
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new CheeseException(e);
        }
        return result;
    }
    
    public abstract ComputerPlayer copy();
    
}
