package com.explosionduck.micemen.calculation.noop;

import com.explosionduck.micemen.domain.CheeseException;
import com.explosionduck.micemen.domain.blocks.Mouse;
import com.explosionduck.micemen.fx.FrameMap;
import com.explosionduck.micemen.fx.MouseGraphic;

public class NoOpFrameMap implements FrameMap {

    private static final String DONT_RENDER_THIS = NoOpFrameMap.class.getName() + " should never be rendered";

    @Override
    public void reset(Mouse mouse) {}

    @Override
    public void walk(Mouse mouse) {}

    @Override
    public float mouseFallOffset(Mouse mouse) {
        return 0;
    }

    @Override
    public MouseGraphic getFrameForMuscle(Mouse mouse) {
        throw new CheeseException(DONT_RENDER_THIS);
    }

    @Override
    public MouseGraphic getFrameForEatingMouse(Mouse mouse) {
        throw new CheeseException(DONT_RENDER_THIS);
    }
}
