package entity.sim;

import model.CheeseException;
import model.CheeseGrid;
import model.SimPoint;
import orders.MouseMove;

public class Mouse extends Block {

	public enum Team {
		RED, BLUE;
	}

	protected Team team;
	private Mouse parent;

	public Mouse(Team team) {
		super();
		this.type = Type.MOUSE;
		this.team = team;
	}

	public Team team() {
		return team;
	}

	@Override
	public Block copy()  {
		return new Mouse(team);
	}

	public MouseMove getMoves(CheeseGrid grid, boolean fallsOnly) throws CheeseException {
		SimPoint origin = grid.get(this);
		int x = origin.x();
		int y = origin.y();

		MouseMove result = new MouseMove(this, origin);
		while (true) {

			if (mustFall(grid, x, y)) {
				y += 1;
				result.add(0, 1);
			} else if (canMove(grid, x, y, team) && fallsOnly) {
				int dir = walkingDir(team);
				x += dir;
				result.add(dir, 0);
			} else {
				break;
			}

			if (!grid.contains(x, y))
				throw new CheeseException("Bad move detected.");

			/**
			 * if mouse escaped, remove it before the next mouse can get stuck
			 * when calculating its moves
			 */
			if (y == grid.height() - 1 && (x == 0 || x == grid.width() - 1)) {
				grid.eliminate(this);
			} else {
				Block empty = grid.get(x, y);
				grid.switcH(this, empty);
			}
		}

		return result;
	}

	private static int walkingDir(Team team) {
		return team == Team.RED ? 1 : -1;
	}

	private static boolean mustFall(CheeseGrid grid, int x, int y) {
		if (x + 1 >= grid.height())
			return false;
		return grid.get(x, y + 1).isEmpty();
	}

	private static boolean canMove(CheeseGrid grid, int x, int y, Team team) {
		int dir = walkingDir(team);
		int newX = x + dir;
		if (newX < 0 || newX >= grid.width())
			return false;
		return grid.get(newX, y).isEmpty();
	}

	public Mouse getOriginal() {
		return (parent == null) ? this : parent.getOriginal();
	}
	
	/**
	 * To help the CPU use the right mouse.
	 */
	public void nullifyParentLinks() {
		parent = null;
	}
}
