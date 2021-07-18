package com.explosionduck.micemen.dagger.config;

import com.explosionduck.micemen.MainGame;
import com.explosionduck.micemen.calculation.simulator.Simulator;
import dagger.Component;

import javax.inject.Singleton;

@Singleton
@Component(modules = {
        BoardControlConfiguration.class,
        MainGameConfiguration.class
})
public interface PreLibGDXInitComponent {

    MainGame buildGame();

    Simulator buildSimulator();
}
