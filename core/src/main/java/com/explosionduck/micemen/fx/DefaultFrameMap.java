package com.explosionduck.micemen.fx;

import com.explosionduck.micemen.domain.blocks.Block;
import com.explosionduck.micemen.domain.blocks.Mouse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultFrameMap implements FrameMap {

    /** len: 10 */
    private static final List<MouseGraphic> EATING_SEQUENCE = List.of(
            MouseGraphic.EAT1, MouseGraphic.EAT1, MouseGraphic.EAT1,
            MouseGraphic.EAT2, MouseGraphic.EAT3, MouseGraphic.STAND,
            MouseGraphic.EAT3, MouseGraphic.STAND,
            MouseGraphic.EAT3, MouseGraphic.STAND);
    /** len: 11 */
    private static final List<MouseGraphic> FLEXING_SEQUENCE = List.of(MouseGraphic.FACE_CAMERA,
            MouseGraphic.FACE_CAMERA, MouseGraphic.FACE_CAMERA, MouseGraphic.FACE_CAMERA,
            MouseGraphic.MUSCLE1, MouseGraphic.MUSCLE2, MouseGraphic.MUSCLE1,
            MouseGraphic.MUSCLE2, MouseGraphic.MUSCLE1,
            MouseGraphic.MUSCLE2, MouseGraphic.FACE_CAMERA);
    private static final float FALL_OFFSET = -12f;
    private static final float FALL_FRAME_MULTIPLIER = 0.25f;

    private final Map<Block, Integer> frameMap = new HashMap<>();

    @Override
    public void reset(Mouse mouse) {
        mouse.setGraphic(MouseGraphic.STAND);
        frameMap.put(mouse, 0);
    }

    /**
     * alternates between walking and standing graphics
     */
    @Override
    public void walk(Mouse mouse) {
        int frame = frameMap.getOrDefault(mouse, 0);

        int next = frame == 0 ? 1 : 0;
        frameMap.put(mouse, next);

        if (next == 1)
            mouse.setGraphic(MouseGraphic.WALK);
        else
            mouse.setGraphic(MouseGraphic.STAND);
    }

    @Override
    public float mouseFallOffset(Mouse mouse) {
        int frame = frameMap.getOrDefault(mouse, 0);
        frameMap.put(mouse, --frame);
        return FALL_OFFSET + frame * FALL_FRAME_MULTIPLIER;
    }

    @Override
    public MouseGraphic getFrameForMuscle(Mouse mouse) {
        int frame = frameMap.getOrDefault(mouse, 0);
        int frameScaled = frame / 25;
        MouseGraphic graphic;

        if (frameScaled >= FLEXING_SEQUENCE.size()) {
            frame = 0;
            graphic = MouseGraphic.FALL; // signals fall
            mouse.setGraphic(graphic);
        } else {
            graphic = FLEXING_SEQUENCE.get(frameScaled);
        }

        frameMap.put(mouse, ++frame);
        return graphic;
    }

    @Override
    public MouseGraphic getFrameForEatingMouse(Mouse mouse) {
        // boolean finished = false;
        int frame = frameMap.getOrDefault(mouse, 0);
        int frameScaled = frame / 10;
        MouseGraphic graphic;

        if (frameScaled >= EATING_SEQUENCE.size()) {
            frame = 0;
            graphic = MouseGraphic.POINT;
            mouse.setGraphic(graphic);
        } else {
            graphic = EATING_SEQUENCE.get(frameScaled);
        }

        frameMap.put(mouse, ++frame);
        return graphic;
    }
}
