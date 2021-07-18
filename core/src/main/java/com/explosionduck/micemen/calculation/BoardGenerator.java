package com.explosionduck.micemen.calculation;

import com.explosionduck.micemen.dagger.interfaces.Stateless;
import com.explosionduck.micemen.domain.Direction;
import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.Point;
import com.explosionduck.micemen.domain.blocks.*;
import com.explosionduck.micemen.util.BoardCalcUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Stateless
public class BoardGenerator {

    private static final Map<Integer, List<Boolean>> NONRANDOM_BLOCK_PLACEMENTS = createStaticBlocksPlacementMap();
    private static final List<Integer> VERBOTEN = Arrays.asList(0, 8, 9, 10, 11, 12, 20);
    private static final int CHEESE_WALL = 7;
    private static final List<Integer> XS = IntStream.range(0, 21).boxed().collect(Collectors.toList());
    private static final List<Integer> YS = IntStream.range(0, 13).boxed().collect(Collectors.toList());

    public void fillGrid(CheeseGrid grid) throws CheeseException {
        int redCount = 0;
        int blueCount = 0;

        var xs = new ArrayList<>(XS);
        var ys = new ArrayList<>(YS);
        Collections.shuffle(xs);
        Collections.shuffle(ys);

        for (Integer x : xs) {
            for (Integer y : ys) {
                Block result = new EmptyBlock();

                if (NONRANDOM_BLOCK_PLACEMENTS.containsKey(x)) {
                    if (NONRANDOM_BLOCK_PLACEMENTS.get(x).get(y))
                        result = new CheeseBlock();
                } else if (columnCheeseCount(grid, x) < CHEESE_WALL && Math.random() > .45) {
                    result = new CheeseBlock();
                } else {
                    boolean placeMouse = Math.random() > .4;
                    if (placeMouse && !VERBOTEN.contains(x)) {
                        if (x < 8
                                && redCount < grid.getMicePerTeam()
                                && BoardCalcUtils.columnMouseCount(grid, x, Team.RED) < 2
                                && rowMouseCount(grid, y, Team.RED) == 0) {
                            result = new Mouse(Team.RED);
                            redCount += 1;
                        } else if (x > 12
                                && blueCount < grid.getMicePerTeam()
                                && BoardCalcUtils.columnMouseCount(grid, x, Team.BLUE) < 2
                                && rowMouseCount(grid, y, Team.BLUE) == 0) {
                            result = new Mouse(Team.BLUE);
                            blueCount += 1;
                        }
                    }
                }

                grid.setInitialBlock(x, y, result);
                if (grid.getBlockAt(x, y) == null)
                    throw new CheeseException("Null block detected");
            }
        }

        /** do 2nd pass to ensure enough mice have been placed */
        secondPass(grid, grid.getMicePerTeam() - redCount, Team.RED);
        secondPass(grid, grid.getMicePerTeam() - blueCount, Team.BLUE);
    }

    private void secondPass(CheeseGrid grid, int difference, Team team) throws CheeseException {
        int start;
        Direction dir;
        if (team == Team.RED) {
            start = 7;
            dir = Direction.LEFT;
        } else {
            start = 13;
            dir = Direction.RIGHT;
        }

        FilteringBlockIterator<EmptyBlock> iter = new FilteringBlockIterator<>(grid, Direction.UP, dir, start, grid.getHeightMax(), EmptyBlock.class)
                .xLimit(5).yLimit(0).max(difference);

        List<EmptyBlock> emptyBlocks = new ArrayList<>();
        iter.forEachRemaining(e -> emptyBlocks.add(e));
        Collections.shuffle(emptyBlocks);

        for (int i = 0; i < difference; i++) {
            Block empty = emptyBlocks.get(i);
            Point esp = grid.getBlockCoordinates(empty);

            grid.addMouse(esp.x, esp.y, team);
        }
    }

    private int columnCheeseCount(CheeseGrid grid, int x) {
        int count = 0;
        for (int y = 0; y < grid.getHeight(); y++) {
            Block block = grid.getBlockAt(x, y);
            if (block != null && block.isCheese())
                count++;
        }
        return count;
    }

    private int rowMouseCount(CheeseGrid grid, int y, Team team) {
        int count = 0;
        for (int x = 0; x < grid.getWidth(); x++) {
            Block block = grid.getBlockAt(x, y);
            if (block != null && block.isMouse() && (team == null || block.isTeam(team)))
                count++;
        }
        return count;
    }

    /**
     * Certain blocks are the same in every single game. This method creates a map of those unchanging block placements.
     */
    private static Map<Integer, List<Boolean>> createStaticBlocksPlacementMap() {
        Map<Integer, List<Boolean>> unchangingBlocks = new HashMap<>();
        for (Integer x : Arrays.asList(1, 5, 9, 11, 15, 19))
            unchangingBlocks.put(x, Arrays.asList(true, false, false, true, false, false, true, false, false, true, false,
                    false, true));
        List<Boolean> empty = Arrays.asList(false, false, false, false, false, false, false, false, false, false, false,
                false, false);
        unchangingBlocks.put(0, empty);
        unchangingBlocks.put(10,
                Arrays.asList(false, true, true, false, true, true, false, true, true, false, true, true, false));
        unchangingBlocks.put(20, empty);
        return Collections.unmodifiableMap(unchangingBlocks);
    }
}
