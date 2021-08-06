package com.explosionduck.micemen.calculation.simulator;

import com.explosionduck.micemen.domain.blocks.Team;

public class SimulationResultAggregate {

    private long time;
    private int redWins;
    private int blueWins;
    private int redHeadStarts;
    private int blueHeadStarts;

    public void count(SimulationResult result) {
        this.time += result.getTime();
        this.redWins += Team.RED == result.getWon() ? 1 : 0;
        this.blueWins += Team.BLUE == result.getWon() ? 1 : 0;
        this.redHeadStarts += Team.RED == result.getStartedAhead() ? 1 : 0;
        this.blueHeadStarts += Team.BLUE == result.getStartedAhead() ? 1 : 0;
    }

    public SimulationResultAggregate combine(SimulationResultAggregate other) {
        var combined = new SimulationResultAggregate();
        combined.time = this.time + other.time;
        combined.redWins = this.redWins + other.redWins;
        combined.blueWins = this.blueWins + other.blueWins;
        combined.redHeadStarts = this.redHeadStarts + other.redHeadStarts;
        combined.blueHeadStarts = this.blueHeadStarts + other.blueHeadStarts;
        return combined;
    }

    @Override
    public String toString() {
        return "total time: " + time +
                " red: " + redWins + " blue: " + blueWins +
                " headstarts:: red: " + redHeadStarts + " blue: " + blueHeadStarts;
    }
}
