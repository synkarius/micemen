package com.explosionduck.micemen.util;

import com.explosionduck.micemen.command.NewGameBootstrapper;
import com.explosionduck.micemen.domain.CheeseGrid;

@FunctionalInterface
public interface GameRestarter {
    NewGameBootstrapper restartGame(CheeseGrid grid);
}
