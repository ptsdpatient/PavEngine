package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.PavScreen.GameWorld.staticObjects;

import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavGameObject.StaticObject;
import com.pavengine.app.TransformTransition;

import java.util.Objects;

public class TimelineTransformData {
    public boolean played = false;
    public GameObject object;
    public String model;
    public TransformTransition initialTransform, finalTransform;
    public float delay;
    public boolean loop;

    public TimelineTransformData(String model, TransformTransition initialTransform, TransformTransition finalTransform, String animation, float delay, boolean loop) {
        this.model = model;
        for(GameObject object : staticObjects) {
            if(Objects.equals(model, object.name)) {
                this.object = object;
            }
        }
        this.initialTransform = initialTransform;
        this.finalTransform = finalTransform;
        this.delay = delay;
        this.loop = loop;
    }
}
