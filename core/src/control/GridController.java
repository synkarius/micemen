package control;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import entity.sim.Block;
import entity.sim.Block.Type;
import entity.sim.Mouse;
import entity.sim.Mouse.Team;
import model.CheeseBlock;
import model.CheeseException;
import model.CheeseGrid;
import model.EmptyBlock;
import model.SimPoint;
import orders.IOrder;
import orders.MouseMove;
import util.NonNullList;

public class GridController {

	private CheeseGrid grid;

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
	private static final List<Integer> VERBOTEN = Arrays.asList(0, 1, 8, 9, 10, 11, 12, 19, 20);
	private static final int CHEESE_WALL = 7;

	public GridController(CheeseGrid grid) {
		this.grid = grid;
	}

	public void fillGrid() throws CheeseException {
		int redCount = 0;
		int blueCount = 0;

		for (int x = 0; x < grid.width(); x++) {
			for (int y = 0; y < grid.height(); y++) {
				Block result = new EmptyBlock();

				if (PLACEMENT.containsKey(x)) {
					if (PLACEMENT.get(x).get(y))
						result = new CheeseBlock();
				} else if (columnCheeseCount(x) < CHEESE_WALL && Math.random() > .45) {
					result = new CheeseBlock();
				} else {
					boolean placeMouse = Math.random() > .4;
					if (placeMouse && !VERBOTEN.contains(x)) {
						if (x < 8 && redCount < grid.micePerTeam() && columnMouseCount(x, Team.RED) < 2
								&& rowMouseCount(y, Team.RED) == 0) {
							result = new Mouse(Team.RED);
							redCount += 1;
						} else if (x > 12 && blueCount < grid.micePerTeam() && columnMouseCount(x, Team.BLUE) < 2
								&& rowMouseCount(y, Team.BLUE) == 0) {
							result = new Mouse(Team.BLUE);
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

		List<Block> emptyBlocks = new BlockIter(grid, Arrays.asList(dir, Direction.UP), start, grid.height() - 1)//.xLimit(5)
				.yLimit(0).type(Type.EMPTY).listLimit(difference).toList();
		Collections.shuffle(emptyBlocks);

		for (int i = 0; i < difference; i++) {
			Block empty = emptyBlocks.get(i);
			SimPoint esp = grid.get(empty);

			grid.fillVacancy(esp.x(), esp.y(), team);
		}
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

	private int columnMouseCount(int x, Team team) {
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

	private int score(Team team) {
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
		boolean poleIsBlocked = grid.X() != null && x == grid.lastPole();
		return !poleIsBlocked && grid.poles()[x];
	}

	public void recalculateMoves() throws CheeseException {
		CheeseGrid copygrid = null;
		Team newActiveTeam = grid.activeTeam() == Team.RED ? Team.BLUE : Team.RED;

		List<IOrder> results = new NonNullList<>();

		while (true) {
			/**
			 * the copygrid-null mechanism here is so we don't keep queueing up
			 * the same set of moves forever
			 */
			if (copygrid == null)
				copygrid = new CheeseGrid(grid);

			boolean red = newActiveTeam == Team.RED;
			Direction dir = red ? Direction.LEFT : Direction.RIGHT;
			int start = red ? copygrid.width() : 0;

			/** first, look for falls */
			List<Mouse> allMice = getAllMiceWithDirection(Direction.UP, dir, start, copygrid.height());
			MouseMove move = findFirstMove(copygrid, allMice, true);
			if (results.add(move))
				continue;

			/** the active team then moves */
			List<Mouse> activeTeam = allMice.stream().filter(mouse -> active(mouse, true)).collect(Collectors.toList());
			move = findFirstMove(copygrid, activeTeam, false);
			if (results.add(move))
				continue;

			/** non-active team last */
			boolean other = newActiveTeam != Team.RED;
			dir = other ? Direction.LEFT : Direction.RIGHT;
			start = other ? copygrid.width() : 0;
			List<Mouse> nonactiveTeam = getAllMiceWithDirection(Direction.UP, dir, start, copygrid.height()).stream()
					.filter(mouse -> active(mouse, false)).collect(Collectors.toList());
			move = findFirstMove(copygrid, nonactiveTeam, false);
			if (results.add(move))
				continue;
			
			/** if no moves found, stop */
			break;
		}
		
		//TODO: do something with orders 'result'
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
		return new BlockIter(grid, Arrays.asList(first, second), startX, startY).type(Type.MOUSE).toList()
				//
				.stream().map(block -> (Mouse) block).collect(Collectors.toList());
	}

	private static List<Mouse> filterForTeam(List<Mouse> mice, Team team) {
		return mice.stream().filter(mouse -> mouse.isTeam(team)).collect(Collectors.toList());
	}

	private List<Mouse> getAllMiceUpDown(boolean active) {
		/**
		 * Gets all mice on team whose turn it is, or whose turn it isn't
		 */
		return getAllMice().stream().filter(mouse -> active(mouse, active)).collect(Collectors.toList());
	}

	private boolean active(Mouse block, boolean getUp) {
		boolean result = grid.activeTeam() == Team.RED && block.isRedMouse()
				|| grid.activeTeam() == Team.BLUE && block.isBlueMouse();
		if (getUp)
			return result;
		else
			return !result;
	}

}
