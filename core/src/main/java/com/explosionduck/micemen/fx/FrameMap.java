package com.explosionduck.micemen.fx;

import com.explosionduck.micemen.calculation.FilteringBlockIterator;
import com.explosionduck.micemen.domain.CheeseGrid;
import com.explosionduck.micemen.domain.Direction;
import com.explosionduck.micemen.domain.blocks.Mouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Tracks which animation frame all blocks are using.
 */
public interface FrameMap {

    void reset(Mouse mouse);

    void walk(Mouse mouse);

    float mouseFallOffset(Mouse mouse);

    MouseGraphic getFrameForMuscle(Mouse mouse);

    MouseGraphic getFrameForEatingMouse(Mouse mouse);

    static void randomMouseEatsCheese(CheeseGrid grid) { // TODO: move this out of here
        if (Math.random() > .9960) {
            FilteringBlockIterator<Mouse> iter = new FilteringBlockIterator<>(grid, Direction.DOWN, Direction.RIGHT, 0, 0, Mouse.class)
                    .filter(mouse -> mouse.getGraphic() == MouseGraphic.POINT);
            List<Mouse> mice = new ArrayList<>();
            iter.forEachRemaining(mice::add);
            if (mice.size() > 0) {
                Collections.shuffle(mice);
                mice.get(0).setGraphic(MouseGraphic.EAT1);
            }
        }
    }
}