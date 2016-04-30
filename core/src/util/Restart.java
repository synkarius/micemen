package util;

import model.CheeseGrid;

@FunctionalInterface
public interface Restart {
    void action(CheeseGrid grid);
}
