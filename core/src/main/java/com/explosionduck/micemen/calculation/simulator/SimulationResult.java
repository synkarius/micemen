package com.explosionduck.micemen.calculation.simulator;

import com.explosionduck.micemen.domain.blocks.Team;

public class SimulationResult {

    private final Team startedAhead;
    private final Team won;
    private final long time;

    public SimulationResult(Team startedAhead, Team won, long time) {
        this.startedAhead = startedAhead;
        this.won = won;
        this.time = time;
    }

    public Team getStartedAhead() {
        return startedAhead;
    }

    public Team getWon() {
        return won;
    }

    public long getTime() {
        return time;
    }
}
