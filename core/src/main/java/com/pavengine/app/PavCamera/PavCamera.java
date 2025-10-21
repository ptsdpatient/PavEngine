package com.pavengine.app.PavCamera;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Vector3;

public abstract class PavCamera {

    public static PerspectiveCamera camera;
    public Vector3 pos = new Vector3();
    public float dist = 8f, yaw = 0f, pitch = 25f;
    public boolean playerCenter = false;

    public PavCamera() {

    }

    public PavCamera(float fov) {


    }

    public abstract void update(float delta);

    public abstract void rotate(float dx, float dy);

    public abstract void pan(float dx, float dy);

    public abstract void zoom(float y);
}
