package com.pavengine.app.PavBounds;

import com.badlogic.gdx.math.Vector3;

public class EntranceBluprint {
    public Vector3 offset, size;
    public Entrance.Type type;
    public Side side;

    public enum Side {LEFT, RIGHT, FRONT, BACK}
}
