package com.explosionduck.micemen.dagger.config;

import com.explosionduck.micemen.calculation.BoardGenerator;
import com.explosionduck.micemen.calculation.BoardScorer;
import com.explosionduck.micemen.calculation.calculator.DefaultGridCalculator;
import com.explosionduck.micemen.calculation.calculator.GridCalculator;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class BoardControlConfiguration {

    @Provides
    @Singleton
    BoardGenerator provideBoardGenerator() {
        return new BoardGenerator();
    }

    @Provides
    @Singleton
    BoardScorer provideBoardScorer() {
        return new BoardScorer();
    }

    @Provides
    @Singleton
    GridCalculator provideGridCalculator(BoardScorer boardScorer) {
        return new DefaultGridCalculator(boardScorer);
    }
}
