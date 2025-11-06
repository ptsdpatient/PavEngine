package com.pavengine.app.PavCamera;


import static com.pavengine.app.PavPlayer.PavPlayer.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

public class IsometricCamera extends PavCamera {
    private Vector3 focus;
    private float minDist = 5f;
    private float maxDist = 50f;

    public IsometricCamera(float fov) {
        super(fov);

        dist = 20f;
        yaw = 45f;
        pitch = 35f;

        focus = new Vector3(0, 0, 0);
        update(0);
    }

    @Override
    public void update(float delta) {
        if (playerCenter && player != null) {
            focus.set(player.pos);
        }

        pos.set(
            focus.x - (float) (dist * Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw))),
            focus.y + (float) (dist * Math.sin(Math.toRadians(pitch))),
            focus.z - (float) (dist * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)))
        );

        camera.position.set(pos);
        camera.lookAt(focus);
        camera.up.set(Vector3.Y);
        camera.update();
    }

    @Override
    public void pan(float dx, float dy) {
        focus.add((new Vector3(camera.direction).crs(Vector3.Y).nor()).scl(dx * dist * 0.02f));
        focus.add((new Vector3(camera.direction).cpy().set(camera.direction.x, 0, camera.direction.z).nor()).scl(dy * dist * 0.02f));
    }

    @Override
    public void rotate(float dx, float dy) {

    }

    @Override
    public void zoom(float amount) {
        dist += amount * 3;
        dist = MathUtils.clamp(dist, minDist, maxDist);
    }
}
