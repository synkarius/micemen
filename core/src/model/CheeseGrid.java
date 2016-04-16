package model;

import java.util.Arrays;
import java.util.List;

import control.Direction;
import control.GridController;
import entity.sim.Block;
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
		for (int x = 0; x < width; x++)
			for (int y = 0; y < height; y++)
				this.grid[x][y] = null;
		// this.cache = new HashMap<>();
		this.poles = new boolean[width];
		this.micePerTeam = micePerTeam;
		this.ctrl = new GridController(this);
	}

	public CheeseGrid(CheeseGrid grid) throws CheeseException {
		this(grid.width(), grid.height(), grid.micePerTeam());
		for (int x = 0; x < width(); x++)
			for (int y = 0; y < height(); y++)
				set(x, y, grid.get(x, y).copy());
		this.activeTeam = grid.activeTeam();
		this.isCopy = true;
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

	public Team activeTeam() {
		return activeTeam;
	}

	public void fillVacancy(int x, int y, Team team) throws CheeseException {
		Block replaced = get(x, y);
		if (!replaced.isEmpty())
			throw new CheeseException("Can't replace a non-empty.");

		Mouse mouse = new Mouse(team);
		grid[x][y] = mouse;
	}

	public void eliminate(Mouse mouse) {
		SimPoint p = get(mouse);
		Block empty = new EmptyBlock();

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
