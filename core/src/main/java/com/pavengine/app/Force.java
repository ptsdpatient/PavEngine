package com.pavengine.app;

import com.badlogic.gdx.math.Vector3;

public class Force {
    public float amplitude;
    float decayRate = 2f;
    Vector3 direction;
    boolean active = false;

    public Force(Vector3 direction, float amplitude) {
        this.amplitude = amplitude;
        this.direction = new Vector3(direction).nor();
    }

    public Vector3 update(float delta, boolean decay) {

        if (decay) amplitude -= decayRate * delta;

        if (amplitude <= 0) {
            amplitude = 0;
            active = false;
        }

        return new Vector3(direction).scl(amplitude * delta);
    }
}
