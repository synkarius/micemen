package model;

import java.util.Arrays;
import java.util.List;

import control.BlockIter;
import control.Direction;
import file.Board;
import graphical.Resource.Graphic;
import gridparts.GfxState;
import gridparts.GridController;
import gridparts.WholeGameRecording;
import model.Block.Type;
import model.Mouse.Team;
import simulate.SimulationNode;

public class CheeseGrid {
    private Block[][]          grid;
    // private Map<Block, SimPoint> cache;
    private boolean[]          poles;
    private int                activePole;
    private Team               activeTeam;
    private int                micePerTeam;
    private Integer            X;
    private Integer            redHand;
    private Integer            blueHand;
    
    private boolean            opponentWasCPU;
    private boolean            isGraphical;
    
    private SimulationNode     lastChosen;
    
    /**
     * break up the jobs of the grid into 4 parts: display, control, recording,
     * model
     */
    private GridController     ctrl;
    private WholeGameRecording recording;
    private GfxState           state;
    
    /**
     * Moves and stores blocks -- does NOT handle graphics
     */
    public CheeseGrid(int width, int height, int micePerTeam) {
        this.grid = new Block[width][height];
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                this.grid[x][y] = null;
            }
        }
        // this.cache = new HashMap<>();
        this.poles = new boolean[width];
        this.micePerTeam = micePerTeam;
        this.ctrl = new GridController(this);
        this.recording = new WholeGameRecording(this);
        this.state = new GfxState(this);
        this.gridID = getID();
    }
    
    public CheeseGrid(CheeseGrid grid) throws CheeseException {
        this(grid.width(), grid.height(), grid.micePerTeam());
        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                Block oldBlock = grid.get(x, y);
                if (oldBlock.type() == Type.MOUSE) {
                    set(x, y, new Mouse((Mouse) oldBlock, this));
                } else if (oldBlock.type() == Type.CHEESE) {
                    set(x, y, new CheeseBlock(this));
                } else if (oldBlock.type() == Type.EMPTY) {
                    set(x, y, new EmptyBlock(this));
                }
            }
        }
        for (int b = 0; b < poles.length; b++)
            poles[b] = grid.poles[b];
        this.activeTeam = grid.activeTeam();
    }
    
    public GfxState state() {
        return state;
    }
    
    private static int nextID = 0;
    
    protected int      gridID;
    
    public int id() {
        return gridID;
    }
    
    private static int getID() {
        if (nextID > 1000000)// 1m
            nextID = 0;
        return nextID++;
    }
    
    public void makeGraphical() {
        this.isGraphical = true;
        BlockIter<Mouse> iter = new BlockIter<>(this, Direction.DOWN, Direction.RIGHT, 0, 0, Mouse.class);
        iter.forEachRemaining(mouse -> mouse.graphic(Graphic.STAND));
        this.state.init();
    }
    
    public boolean isGraphical() {
        return isGraphical;
    }
    
    public GridController ctrl() {
        return ctrl;
    }
    
    public int width() {
        return grid.length;
    }
    
    public int height() {
        return grid[0].length;
    }
    
    public int hMax() {
        return height() - 1;
    }
    
    public int wMax() {
        return width() - 1;
    }
    
    public Team activeTeam() {
        return activeTeam;
    }
    
    public void activeTeam(Team team) {
        this.activeTeam = team;
    }
    
    public void fillVacancy(int x, int y, Team team) throws CheeseException {
        Block replaced = get(x, y);
        if (!replaced.isEmpty())
            throw new CheeseException("Can't replace a non-empty.");
        
        Mouse mouse = new Mouse(team, this);
        grid[x][y] = mouse;
    }
    
    public void eliminate(Mouse mouse) {
        int x = mouse.team == Team.RED ? wMax() : 0;
        int y = hMax();
        
        Block empty = new EmptyBlock(this);
        grid[x][y] = empty;
    }
    
    public SimPoint get(Block block) {
        for (int x = 0; x < width(); x++)
            for (int y = 0; y < height(); y++)
                if (get(x, y).equals(block))
                    return new SimPoint(x, y);
        return null;
    }
    
    public void shift(Direction dir, int x) {
        Block[] copy = grid[x].clone();
        
        if (dir == Direction.DOWN) {
            for (int y2 = 1; y2 < height(); y2++)
                grid[x][y2] = copy[y2 - 1];
            grid[x][0] = copy[copy.length - 1];
        } else {
            for (int y2 = height() - 2; y2 >= 0; y2--)
                grid[x][y2] = copy[y2 + 1];
            grid[x][grid[x].length - 1] = copy[0];
            
        }
        
        lastChosen = new SimulationNode(x, dir, 0);
    }
    
    public void switcH(SimPoint a, SimPoint b) {
        switcH(a.x(), a.y(), b.x(), b.y());
    }
    
    public void switcH(int ax, int ay, int bx, int by) {
        Block blockA = get(ax, ay);
        Block blockB = get(bx, by);
        
        grid[ax][ay] = blockB;
        grid[bx][by] = blockA;
    }
    
    /** for initial sets only -- no longer for updates */
    public void set(int x, int y, Block block) throws CheeseException {
        if (grid[x][y] != null)
            throw new CheeseException("Do not use 'set' for updates.");
        
        grid[x][y] = block;
    }
    
    public Block get(int x, int y) {
        return grid[x][y];
    }
    
    public boolean contains(int x, int y) {
        return x >= 0 && x < width() && y >= 0 && y < height();
    }
    
    public boolean contains(SimPoint pt) {
        return contains(pt.x(), pt.y());
    }
    
    public List<Block> column(int x) {
        return Arrays.asList(grid[x]);
    }
    
    public int micePerTeam() {
        return micePerTeam;
    }
    
    public boolean[] poles() {
        return poles;
    }
    
    /** formerly "activeX" */
    public Integer X() {
        return X;
    }
    
    public void X(Integer x) {
        X = x;
    }
    
    public int activePole() {
        return activePole;
    }
    
    public void activePole(int x) {
        activePole = x;
    }
    
    public SimulationNode lastChosen() {
        return lastChosen;
    }
    
    public Integer redHand() {
        return redHand;
    }
    
    public void redHand(Integer redHand) {
        this.redHand = redHand;
    }
    
    public Integer blueHand() {
        return blueHand;
    }
    
    public void blueHand(Integer blueHand) {
        this.blueHand = blueHand;
    }
    
    public WholeGameRecording recording() {
        return recording;
    }
    
    public static CheeseGrid getNewDefault() {
        return new CheeseGrid(21, 13, 12);
    }
    
    public void opponentWasCPU(boolean b) {
        this.opponentWasCPU = b;
    }
    
    public boolean opponentWasCPU() {
        return opponentWasCPU;
    }
    
    @Override
    public String toString() {
        return Board.getBoardString(this, true);
    }
}
