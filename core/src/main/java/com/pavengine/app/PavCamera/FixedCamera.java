package com.pavengine.app.PavCamera;


import com.badlogic.gdx.math.Vector3;

// ==================== Fixed ====================
public class FixedCamera extends PavCamera {

    private Vector3 fixedPos;
    private Vector3 fixedTarget;

    public FixedCamera(float fov, Vector3 pos, Vector3 target) {
        super(fov);
        this.fixedPos = new Vector3(pos);
        this.fixedTarget = new Vector3(target);
    }

    public void update(float delta) {
        camera.position.set(fixedPos);
        camera.lookAt(fixedTarget);
        camera.up.set(Vector3.Y);
        camera.update();
    }

    @Override
    public void rotate(float dx, float dy) {

    }

    @Override
    public void pan(float dx, float dy) {

    }

    @Override
    public void zoom(float y) {

    }
}
