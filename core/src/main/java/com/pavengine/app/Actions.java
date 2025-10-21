package com.pavengine.app;

public class Actions {
    public ObjectAction action;
    public float elapsed = 0;
    public float duration;
    public float eased = 0;
    public float amplitude;
    public float lastEased = 0;
    public Direction direction;
    AnimationCurve curve;

    public Actions(ObjectAction action, AnimationCurve curve, Direction direction, float amplitude, float duration) {
        this.action = action;
        this.amplitude = amplitude;
        this.curve = curve;
        this.duration = duration;
        this.direction = direction;
    }

    public void update(float delta) {
        elapsed += delta;
        eased = curve.apply(Math.min(1f, elapsed / duration));
    }
}
