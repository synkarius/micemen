package com.explosionduck.micemen.testing.tests;

import com.explosionduck.micemen.test.GridAssert;
import com.explosionduck.micemen.test.GridTestException;
import com.explosionduck.micemen.test.TestGrids;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class GridTestUtilsTests {

    /**
     * Comparing a grid to a copy of itself should not error on assertEquals.
     * @throws GridTestException
     */
    @Test
    public void testIdentity() throws GridTestException {
        GridAssert.assertEquals(TestGrids.b19x13OneCheeseAt18_2(), TestGrids.b19x13OneCheeseAt18_2());
    }

    /**
     * Mismatching types should throw a GridTestException.
     */
    @Test
    public void testMismatchingType() {
        Assertions.assertThrows(GridTestException.class,
                () -> GridAssert.assertEquals(TestGrids.b19x13OneCheeseAt18_2(), TestGrids.b19x13OneBlueAt18_2()));
    }

    /**
     * Mismatching teams should throw a GridTestException.
     */
    @Test
    public void testMismatchingTeam() {
        Assertions.assertThrows(GridTestException.class,
                () -> GridAssert.assertEquals(TestGrids.b19x13OneRedAt18_2(), TestGrids.b19x13OneBlueAt18_2()));
    }
}
