package com.explosionduck.micemen.calculation.simulator;

import com.explosionduck.micemen.dagger.config.DaggerPreLibGDXInitComponent;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class SimulatorTest {

    /**
     * Tests that the main game logic is producing wins/losses at all, not just ties.
     */
    @Test
    public void testThatSimulationWorks() {
        assertTrue(DaggerPreLibGDXInitComponent.create()
                .buildSimulator()
                .simulate(15) // 15 games should be well more then adequate
                .map(SimulationResult::getWon)
                .anyMatch(Objects::nonNull));
    }
}