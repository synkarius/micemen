package com.explosionduck.micemen.domain;

import com.explosionduck.micemen.domain.blocks.Block;
import com.explosionduck.micemen.domain.blocks.EmptyBlock;
import com.explosionduck.micemen.domain.blocks.Mouse;
import com.explosionduck.micemen.domain.blocks.Team;
import com.explosionduck.micemen.io.FileIO;

import java.util.Arrays;

public class CheeseGrid {

    private static final Integer STANDARD_BOARD_WIDTH = 21;
    private static final Integer STANDARD_BOARD_HEIGHT = 13;
    private static final Integer STANDARD_TEAM_SIZE = 12;

    private final Block[][] grid;
    private final int micePerTeam;
    private final boolean[] poles;
    //
    private int activePole;
    private Team activeTeam;
    private Integer verbotenColumn;
    private Integer redHand;
    private Integer blueHand;
    private boolean opponentWasCPU;
    private int opponentLevel;
    private boolean isGraphical;

    /**
     * Main private constructor.
     */
    private CheeseGrid(int width, int height, int micePerTeam) {
        this.grid = new Block[width][height];
        this.poles = new boolean[width];
        this.micePerTeam = micePerTeam;
    }

    /**
     * Copy constructor. Heavily used in simulations.
     */
    public CheeseGrid(CheeseGrid grid) throws CheeseException { // TODO: replace this copy constructor with a builder?
        this(grid.getWidth(), grid.getHeight(), grid.getMicePerTeam());
        for (int x = 0; x < this.getWidth(); x++) {
            this.grid[x] = Arrays.copyOf(grid.grid[x], grid.grid[x].length);
        }
        for (int i = 0; i < this.poles.length; i++) {
            this.poles[i] = grid.poles[i];
        }

        this.activeTeam = grid.getActiveTeam();
    }

    public static CheeseGrid getNewDefault() {
        return new CheeseGrid(STANDARD_BOARD_WIDTH, STANDARD_BOARD_HEIGHT, STANDARD_TEAM_SIZE);
    }

    ///////////// ----------- simple setters


    public void setActiveTeam(Team team) {
        this.activeTeam = team;
    }

    public void setVerbotenColumn(Integer verbotenColumn) {
        this.verbotenColumn = verbotenColumn;
    }

    public void setActivePole(int x) {
        activePole = x;
    }

    public void setRedHand(Integer redHand) {
        this.redHand = redHand;
    }

    public void setBlueHand(Integer blueHand) {
        this.blueHand = blueHand;
    }

    public void setOpponentWasCPU(boolean b) {
        this.opponentWasCPU = b;
    }

    public void setOpponentLevel(int opponentLevel) {
        this.opponentLevel = opponentLevel;
    }

    ///////////// ----------- simple getters

    public boolean isGraphical() {
        return isGraphical;
    }

    public boolean getOpponentWasCPU() {
        return opponentWasCPU;
    }

    public int getOpponentLevel() {
        return opponentLevel;
    }

    public int getWidth() {
        return grid.length;
    }

    public int getHeight() {
        return grid[0].length;
    }

    public int getMicePerTeam() {
        return micePerTeam;
    }

    public boolean[] getPoles() {
        return poles;
    }

    public Integer getVerbotenColumn() {
        return verbotenColumn;
    }

    public int getActivePole() {
        return activePole;
    }

    public Integer getRedHand() {
        return redHand;
    }

    public Integer getBlueHand() {
        return blueHand;
    }

    ///////////// ----------- derived getters

    public int getHeightMax() {
        return getHeight() - 1;
    }

    public int getWidthMax() {
        return getWidth() - 1;
    }

    public Team getActiveTeam() {
        return activeTeam;
    }

    public boolean coordinatesAreValid(int x, int y) { // TODO: rename this, 'contains' is completely wrong for what it's doing
        return x >= 0 && x < getWidth() && y >= 0 && y < getHeight();
    }

    public Block getBlockAt(int x, int y) {
        return grid[x][y];
    }

    public Point getBlockCoordinates(Block block) { // TODO: rework this so it's not O(n^2)
        for (int x = 0; x < getWidth(); x++) {
            for (int y = 0; y < getHeight(); y++) {
                if (this.getBlockAt(x, y).equals(block))
                    return new Point(x, y);
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return FileIO.serializeGrid(this, true);
    }

    ///////////// ----------- actions -- move this out of here?

    /**
     * Adds a mouse to the board in an empty space at the beginning of a game.
     */
    public void addMouse(int x, int y, Team team) throws CheeseException {
        Block replaced = getBlockAt(x, y);
        if (!replaced.isEmpty())
            throw new CheeseException("Can't replace a non-empty.");

        Mouse mouse = new Mouse(team);
        grid[x][y] = mouse;
    }

    public void deleteMouse(Mouse mouse) {
        int x = mouse.getTeam() == Team.RED ? getWidthMax() : 0;
        int y = getHeightMax();

        Block empty = new EmptyBlock();
        grid[x][y] = empty;
    }

    public void shiftColumn(Direction dir, int x) {
        Block[] copy = Arrays.copyOf(grid[x], grid[x].length);

        if (dir == Direction.DOWN) {
            for (int y2 = 1; y2 < getHeight(); y2++)
                grid[x][y2] = copy[y2 - 1];
            grid[x][0] = copy[copy.length - 1];
        } else {
            for (int y2 = getHeight() - 2; y2 >= 0; y2--)
                grid[x][y2] = copy[y2 + 1];
            grid[x][grid[x].length - 1] = copy[0];
        }
    }

    public void switchBlocks(Point a, Point b) {
        switchBlocks(a.x, a.y, b.x, b.y);
    }

    public void switchBlocks(int ax, int ay, int bx, int by) {
        Block blockA = getBlockAt(ax, ay);
        Block blockB = getBlockAt(bx, by);

        grid[ax][ay] = blockB;
        grid[bx][by] = blockA;
    }

    /**
     * for initial sets only -- no longer for updates
     */
    public void setInitialBlock(int x, int y, Block block) throws CheeseException {
        if (grid[x][y] != null)
            throw new CheeseException("Do not use 'set' for updates.");

        grid[x][y] = block;
    }

    public void makeGraphical() {
        this.isGraphical = true;
    }
}
