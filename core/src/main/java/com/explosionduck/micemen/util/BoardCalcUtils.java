package com.explosionduck.micemen.util;

import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.Block;
import com.explosionduck.micemen.domain.blocks.Team;

public class BoardCalcUtils {

    public static int columnMouseCount(CheeseGrid grid, int x, Team team) {
        int count = 0;
        for (int y = 0; y < grid.getHeight(); y++) {
            Block block = grid.getBlockAt(x, y);
            if (block != null && block.isMouse() && (team == null || block.isTeam(team)))
                count++;
        }
        return count;
    }

    public static boolean poleIsAvailable(CheeseGrid grid, int x) {
        boolean poleIsBlocked = grid.getVerbotenColumn() != null && x == grid.getVerbotenColumn();
        return !poleIsBlocked && grid.getPoles()[x];
    }
}
