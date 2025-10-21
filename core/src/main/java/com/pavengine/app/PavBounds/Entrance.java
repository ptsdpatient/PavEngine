package com.pavengine.app.PavBounds;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;

public class Entrance {
    public Type type = Type.DOOR;
    public PavBounds bounds = new PavBounds();
    public boolean open = false;

    public Entrance() {

    }

    public Entrance(Type type, OrientedBoundingBox bounds, boolean open) {
        this.type = type;
        this.bounds = new PavBounds(bounds);
        this.open = open;
    }

    public boolean contains(Vector3 position) {
        return open && bounds.contains(position);
    }

    public enum Type {DOOR, WINDOW, VENT}


}
