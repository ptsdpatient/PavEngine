package com.pavengine.app;

public enum AnimationCurve {
    LINEAR,
    EASE_IN,
    EASE_OUT,
    EASE_IN_OUT,
    BOUNCE,
    ELASTIC,
    EXPONENTIAL_IN,
    BACK;

    public float apply(float t) {
        switch (this) {
            case EASE_IN:
                return t * t; // quadratic ease-in
            case EASE_OUT:
                return t * (2 - t); // quadratic ease-out
            case EASE_IN_OUT:
                return (t < 0.5f) ? 2 * t * t : -1 + (4 - 2 * t) * t; // smooth step
            case BOUNCE:
                return (float) Math.abs(Math.sin(6.28f * (t + 1) * (1 - t)) * (1 - t));
            case ELASTIC:
                return (float) (Math.pow(2, -10 * t) * Math.sin((t - 0.075) * (2 * Math.PI) / 0.3) + 1);
            case BACK:
                float s = 1.70158f;
                return t * t * ((s + 1) * t - s);
            case EXPONENTIAL_IN:
                return (float) ((t == 0f) ? 0f : Math.pow(2, 10 * (t - 1)));
            default:
                return t;
        }
    }
}
