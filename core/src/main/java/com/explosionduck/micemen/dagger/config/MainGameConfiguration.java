package com.explosionduck.micemen.dagger.config;

import com.explosionduck.micemen.MainGame;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import com.explosionduck.micemen.calculation.BoardGenerator;
import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.calculation.simulator.Simulator;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Module
public class MainGameConfiguration {

    @Provides
    @Singleton
    ExecutorService provideExecutorService() {
        return Executors.newWorkStealingPool();
    }

    @Provides
    @Singleton
    MainGame provideMainGame(
            ExecutorService executorService,
            GridCalculator gridCalculator,
            BoardGenerator boardGenerator,
            BoardScorer boardScorer) {
        return new MainGame(executorService, gridCalculator, boardGenerator, boardScorer);
    }

    @Provides
    Simulator provideSimulator(
            ExecutorService executorService,
            GridCalculator gridCalculator,
            BoardGenerator boardGenerator,
            BoardScorer boardScorer) {
        return new Simulator(executorService, gridCalculator, boardGenerator, boardScorer);
    }
}
