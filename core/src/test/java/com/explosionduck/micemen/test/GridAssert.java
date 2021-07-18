package com.explosionduck.micemen.test;

import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.BlockType;
import com.explosionduck.micemen.domain.blocks.Mouse;
import com.explosionduck.micemen.domain.blocks.Block;


public class GridAssert {

    public static void assertEquals(CheeseGrid a, CheeseGrid b) throws GridTestException {
        if (a.getWidth() != b.getWidth() || a.getHeight() != b.getHeight()) {
            throw new GridTestException("Grids have different dimensions");
        }

        for (int x = 0; x<a.getWidthMax(); x++) {
            for (int y = 0; y<a.getHeightMax(); y++) {
                Block aBlock = a.getBlockAt(x, y);
                Block bBlock = b.getBlockAt(x, y);
                boolean sameType = aBlock.getBlockType() == bBlock.getBlockType();
                if (!sameType) {
                    throw new GridTestException("Grids have unequal block type at (" + x + ", " + y +").");
                }
                if (BlockType.MOUSE == aBlock.getBlockType() && BlockType.MOUSE == bBlock.getBlockType()) {
                    Mouse aBlockMouse = Mouse.class.cast(aBlock);
                    Mouse bBlockMouse = Mouse.class.cast(bBlock);
                    boolean sameTeam = aBlockMouse.getTeam() == bBlockMouse.getTeam();
                    if (!sameTeam) {
                        throw new GridTestException("Grids are unequal teams at (" + x + ", " + y +").");
                    }
                }
            }
        }
    }
}
