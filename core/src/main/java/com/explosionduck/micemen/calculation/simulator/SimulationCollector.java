package com.explosionduck.micemen.calculation.simulator;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

import static java.util.function.Function.identity;

public class SimulationCollector
        implements Collector<SimulationResult, SimulationResultAggregate, SimulationResultAggregate> {

    @Override
    public Supplier<SimulationResultAggregate> supplier() {
        return SimulationResultAggregate::new;
    }

    @Override
    public BiConsumer<SimulationResultAggregate, SimulationResult> accumulator() {
        return SimulationResultAggregate::count;
    }

    @Override
    public BinaryOperator<SimulationResultAggregate> combiner() {
        return SimulationResultAggregate::combine;
    }

    @Override
    public Function<SimulationResultAggregate, SimulationResultAggregate> finisher() {
        return identity();
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Set.of(Characteristics.IDENTITY_FINISH, Characteristics.UNORDERED);
    }
}
