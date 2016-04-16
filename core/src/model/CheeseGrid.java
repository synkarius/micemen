package model;

import java.util.Arrays;
import java.util.List;

import control.Direction;
import control.GridController;
import entity.sim.Block;
import entity.sim.Block.Type;
import entity.sim.Mouse;
import entity.sim.Mouse.Team;

public class CheeseGrid {
	private Block[][] grid;
	// private Map<Block, SimPoint> cache;
	private boolean[] poles;
	private int lastPole;
	private Team activeTeam;
	private int micePerTeam;
	private Integer X;

	private boolean isCopy;
	private boolean isLoaded;
	private boolean opponentWasCPU;

	private GridController ctrl;

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
		this.activeTeam = grid.activeTeam();
		this.isCopy = true;
	}

	private static int nextID = 0;

	protected int gridID;

	public int id() {
		return gridID;
	}

	private static int getID() {
		if (nextID > 1000000)// 1m
			nextID = 0;
		return nextID++;
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

	public void fillVacancy(int x, int y, Team team) throws CheeseException {
		Block replaced = get(x, y);
		if (!replaced.isEmpty())
			throw new CheeseException("Can't replace a non-empty.");

		Mouse mouse = new Mouse(team, this);
		grid[x][y] = mouse;
	}

	public void eliminate(Mouse mouse) {
		SimPoint p = get(mouse);
		Block empty = new EmptyBlock(this);

		grid[p.x()][p.y()] = empty;
	}

	public SimPoint get(Block block) {
		for (int x = 0; x < width(); x++)
			for (int y = 0; y < height(); y++)
				if (get(x, y).equals(block))
					return new SimPoint(x, y);
		return null;
	}

	public void shift(Direction dir, int x) {
		Block A = null;
		Block B = null;
		if (dir == Direction.DOWN) {
			for (int y2 = 0; y2 < height(); y2++) {
				if (y2 != height() - 1) {
					// if y2 is not the second to last
					A = get(x, y2);
					grid[x][y2] = B;
					B = get(x, y2 + 1);
					grid[x][y2 + 1] = A;
				} else {
					// y2 is the last block
					grid[x][0] = B;
				}
			}
		} else {
			for (int y2 = height() - 1; y2 >= 0; y2--) {
				if (y2 != 0) {
					// if y2 is not the second to last
					A = get(x, y2);
					grid[x][y2] = B;
					B = get(x, y2 - 1);
					grid[x][y2 - 1] = A;
				} else {
					// y2 is the last block
					grid[x][height() - 1] = B;
				}
			}
		}
	}

	public void switcH(Block blockA, Block blockB) {
		SimPoint a = get(blockA);
		SimPoint b = get(blockB);

		grid[a.x()][a.y()] = blockB;
		grid[b.x()][b.y()] = blockA;
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

	public int lastPole() {
		return lastPole;
	}
}
