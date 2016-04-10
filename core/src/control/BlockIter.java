package control;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import entity.sim.Block;
import entity.sim.Block.Type;
import model.CheeseGrid;

public class BlockIter implements Iterator<Block> {

	private int x;
	private int y;

	private int ox;// original
	private int oy;

	private int lx;// limit
	private int ly;
	private int tx;// traversed
	private int ty;

	private CheeseGrid grid;
	private List<Direction> dirs;

	private Type blocktype;

	/** controls how many get returned */
	private int listLimit;

	public BlockIter(CheeseGrid grid, List<Direction> dirs, int x, int y) {
		this.grid = grid;
		this.dirs = dirs;
		this.x = x;
		this.y = y;
	}

	/** optional param */
	public BlockIter xLimit(int lx) {
		this.lx = lx;
		return this;
	}

	/** optional param */
	public BlockIter yLimit(int ly) {
		this.ly = ly;
		return this;
	}

	/** optional param */
	public BlockIter type(Type type) {
		this.blocktype = type;
		return this;
	}

	/** optional param */
	public BlockIter listLimit(int listLimit) {
		this.listLimit = listLimit;
		return this;
	}

	private boolean notHitListLimit(List<Block> list) {
		return listLimit == 0 || list.size() < listLimit;
	}

	public List<Block> toList() {
		List<Block> result = new ArrayList<>();
		while (this.hasNext() && notHitListLimit(result)) {
			Block block = this.next();
			if (blocktype == null || blocktype == block.type())
				result.add(block);
		}
		return result;
	}

	@Override
	public boolean hasNext() {
		return currentBlock() != null;
	}

	// TODO: cache currentblock??

	@Override
	public Block next() {
		Block next = currentBlock();
		setNextBlock();
		return next;
	}

	private Block currentBlock() {
		boolean edgeOfGrid = x < 0 || x >= grid.width() || y < 0 || y > grid.height();
		boolean limits = false;
		boolean xLimitExists = lx != 0;
		boolean exceedXLimit = tx + 1 > lx;
		boolean yLimitExists = ly != 0;
		boolean exceedYLimit = ty + 1 > ly;
		if (xLimitExists && yLimitExists) {
			if (tx == lx && ty == ly) {
				limits = false;
			} else {
				limits = exceedXLimit && exceedYLimit;
			}
		} else if (xLimitExists) {
			if (tx == lx) {
				limits = false;
			} else {
				limits = exceedXLimit;
			}
		} else if (yLimitExists) {
			if (ty == ly) {
				limits = false;
			} else {
				limits = exceedYLimit;
			}
		}

		if (edgeOfGrid || limits) {
			return null;
		}

		return grid.get(x, y);
	}

	private void setNextBlock() {
		int cx = x;
		int cy = y;

		Direction d1 = dirs.get(0);
		Direction d2 = null;
		if (dirs.size() > 1) {
			d2 = dirs.get(1);
		}

		boolean d1IsXAxis = d1 == Direction.LEFT || d1 == Direction.RIGHT;
		boolean d1IsYAxis = d1 == Direction.UP || d1 == Direction.DOWN;
		boolean hitLimit1 = (lx != 0 && d1IsXAxis && tx + 1 > lx) || (ly != 0 && d1IsYAxis && ty + 1 > ly);

		switch (d1) {
		case UP:
			cy -= 1;
			ty += 1;
			break;
		case DOWN:
			cy += 1;
			ty += 1;break;
		case LEFT:
			cx -= 1;
			tx += 1;break;
		case RIGHT:
			cx += 1;
			tx += 1;break;
		default:
			cy += 0;
		}

		if (d2 != null) {
			boolean edgeOfGrid = cx < 0 || cx >= grid.width() || cy < 0 || cy >= grid.height();

			if (edgeOfGrid || hitLimit1) {
				boolean d2IsXAxis = d2 == Direction.LEFT || d2 == Direction.RIGHT;
				boolean d2IsYAxis = d2 == Direction.UP || d2 == Direction.DOWN;
				boolean hitLimit2 = ((lx != 0) && d2IsXAxis && (tx + 1 > lx)) || (ly != 0 && d2IsYAxis && ty + 1 > ly);

				if (!hitLimit2) {
					switch (d2) {
					case UP:
						cx = ox;
						tx = 0;
						cy -= 1;
						ty += 1;break;
					case DOWN:
						cx = ox;
						tx = 0;
						cy += 1;
						ty += 1;break;
					case LEFT:
						cy = oy;
						ty = 0;
						cx -= 1;
						tx += 1;break;
					case RIGHT:
						cy = oy;
						ty = 0;
						cx += 1;
						tx += 1;break;
					default:
						cy += 0;
					}
				}

			}
		}

		x = cx;
		y = cy;
	}

}
