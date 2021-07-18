package com.explosionduck.micemen.domain.grid;

import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.test.GridAssert;
import com.explosionduck.micemen.test.GridTestException;
import com.explosionduck.micemen.test.TestGrids;
import org.junit.jupiter.api.Test;

public class CheeseGridTest {

    @Test
    public void testSwitching() throws GridTestException {
        CheeseGrid testGrid = TestGrids.b19x13OneCheeseAt18_2();
        testGrid.switchBlocks(18, 2, 18, 3);

        GridAssert.assertEquals(TestGrids.b19x13OneCheeseAt18_3(), testGrid);
    }
}