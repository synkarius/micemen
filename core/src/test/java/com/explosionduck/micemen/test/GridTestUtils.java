package com.explosionduck.micemen.test;

import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.blocks.CheeseBlock;
import com.explosionduck.micemen.domain.blocks.EmptyBlock;
import com.explosionduck.micemen.domain.blocks.Mouse;
import com.explosionduck.micemen.domain.blocks.Team;

public class GridTestUtils {

    public static CheeseGrid makeGridFromString(String stringGrid) {
        CheeseGrid result = CheeseGrid.getNewDefault();
        String[] rows = stringGrid.split("\n");
        for (int y=0; y<rows.length; y++) {
            String row = rows[y];
            char[] columns = row.toCharArray();
            for (int x=0; x<columns.length; x++) {
                result.setInitialBlock(x, y, switch (columns[x]) {
                    case 'r' -> new Mouse(Team.RED);
                    case 'b' -> new Mouse(Team.BLUE);
                    case '#' -> new CheeseBlock();
                    case '.' -> new EmptyBlock();
                    default -> throw new CheeseException("Unsupported block type: " + columns[x]);
                });
            }
        }
        return result;
    }
}
