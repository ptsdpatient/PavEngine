package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.PavScreen.GameWorld.staticObjects;

import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavGameObject.StaticObject;

import java.util.Objects;

public class TimelineAnimateData {
    boolean played = false;
    GameObject object;
    String model;
    String animation;
    float delay;
    boolean loop;

    public TimelineAnimateData(String model, String animation, float delay, boolean loop) {
        this.model = model;
        for(GameObject object : staticObjects) {
            if(Objects.equals(model, object.name)) {
                this.object = object;
            }
        }
        this.animation = animation;
        this.delay = delay;
        this.loop = loop;
    }
}
