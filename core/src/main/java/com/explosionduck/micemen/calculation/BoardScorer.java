package com.explosionduck.micemen.calculation;

import com.explosionduck.micemen.dagger.interfaces.Stateless;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.Block;
import com.explosionduck.micemen.domain.blocks.Team;

@Stateless
public class BoardScorer {

    private static final int BLUE_OFFSET = 1;
    private static final int MOUSE_PRESENCE = 20;

    /**
     * does-everything version
     */
    public Scores scores(CheeseGrid grid, boolean doValue) {
        Scores result = new Scores();
        int redsLeft = 0;
        int bluesLeft = 0;

        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Block block = grid.getBlockAt(x, y);
                if (block.isRedMouse())
                    redsLeft++;
                else if (block.isBlueMouse())
                    bluesLeft++;

                if (doValue && block.isMouse()) {
                    result.redBoardValue += x;
                    result.blueBoardValue += grid.getWidth() - x - BLUE_OFFSET;
                }
            }
        }

        result.redScore = grid.getMicePerTeam() - redsLeft;
        result.blueScore = grid.getMicePerTeam() - bluesLeft;
        if (doValue) {
            result.redBoardValue += (result.redScore - result.blueScore) * MOUSE_PRESENCE;
            result.blueBoardValue += (result.blueScore - result.redScore) * MOUSE_PRESENCE;
        }

        return result;
    }

    /**
     * simple version
     */
    public int score(CheeseGrid grid, Team team) {
        int miceLeft = 0;
        for (int x = 0; x < grid.getWidth(); x++) {
            for (int y = 0; y < grid.getHeight(); y++) {
                Block block = grid.getBlockAt(x, y);
                if (block.isMouse() && block.isTeam(team)) {
                    miceLeft++;
                }
            }
        }
        return grid.getMicePerTeam() - miceLeft;
    }

    public static class Scores {
        /**
         * escapees
         */
        public int redScore;
        public int blueScore;
        /**
         * board values
         */
        public int redBoardValue;
        public int blueBoardValue;
    }
}
