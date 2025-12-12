package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.PavScreen.GameWorld.staticObjects;

import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavGameObject.StaticObject;

import java.util.Objects;

public class TimelineAnimateData {
    public boolean played = false;
    public GameObject object;
    public String model;
    public String animation;
    public float delay;
    public boolean loop;

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
