package com.explosionduck.micemen.fx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;

public final class Sounds {
    public static final Sound HI = Gdx.audio.newSound(Gdx.files.internal("hi.wav"));
    public static final Sound LO = Gdx.audio.newSound(Gdx.files.internal("lo.wav"));
    public static final Sound LO_2 = Gdx.audio.newSound(Gdx.files.internal("lo2.wav"));
    public static final Sound JUMP = Gdx.audio.newSound(Gdx.files.internal("jump.wav"));
}
