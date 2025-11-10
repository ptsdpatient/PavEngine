package com.pavengine.app.PavCamera;

import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public abstract class PavCamera {

    public static PerspectiveCamera camera;
    public Vector3 pos = new Vector3();
    public float dist = 8f, yaw = 0f, pitch = 25f, roll = 0f;
    public boolean playerCenter = false;

    public PavCamera() {

    }

    public PavCamera(float fov) {


    }

    public void setDirection(Vector3 dir) {
        dir.nor();

        yaw = MathUtils.atan2(dir.x, dir.z) * MathUtils.radiansToDegrees;
        pitch = MathUtils.asin(dir.y) * MathUtils.radiansToDegrees;

        camera.direction.set(dir);
        print(dir);
        camera.update();
    }

    public abstract void update(float delta);

    public abstract void rotate(float dx, float dy);

    public abstract void pan(float dx, float dy);

    public abstract void zoom(float y);
}
