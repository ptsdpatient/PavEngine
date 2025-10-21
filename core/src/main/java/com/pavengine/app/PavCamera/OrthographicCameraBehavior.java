package com.pavengine.app.PavCamera;


import static com.pavengine.app.PavPlayer.PavPlayer.player;

import com.badlogic.gdx.math.Vector3;

// ==================== Orthographic ====================
public class OrthographicCameraBehavior extends PavCamera {

    private float size;

    public OrthographicCameraBehavior(float size) {
        super(60f); // fov not used in ortho, but needed for constructor
        this.size = size;
    }

    public void update(float delta) {
        Vector3 focus = (player != null) ? player.pos : Vector3.Zero;

        camera.position.set(focus.x, focus.y + 20f, focus.z);

        camera.lookAt(focus);
        camera.up.set(Vector3.Z); // top-down for 2D-like
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
