package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.PavScreen.GameWorld.staticObjects;

import com.pavengine.app.PavGameObject.GameObject;

import java.util.Objects;

public class TimelineSoundData {
    boolean played = false;

    public String sound;
    public float delay;

    public TimelineSoundData(String sound, float delay) {
        this.sound = sound;
        this.delay = delay;
    }
}
