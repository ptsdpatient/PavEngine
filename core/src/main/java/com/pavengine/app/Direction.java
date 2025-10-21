package com.pavengine.app;

import com.badlogic.gdx.math.Vector3;

public enum Direction {
    RIGHT(new Vector3(0, 1, 0)),
    LEFT(new Vector3(0, -1, 0)),
    UP(new Vector3(0, -1, 0)),
    DOWN(new Vector3(0, -1, 0));

    public final Vector3 dir;

    Direction(Vector3 dir) {
        this.dir = dir;
    }
}
