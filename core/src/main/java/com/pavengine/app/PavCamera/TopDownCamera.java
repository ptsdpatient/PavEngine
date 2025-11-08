package com.pavengine.app.PavCamera;


import static com.pavengine.app.PavEngine.enableMapEditor;
import static com.pavengine.app.PavPlayer.PavPlayer.player;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


public class TopDownCamera extends PavCamera {

    boolean playerCenter;

    public TopDownCamera(float fov, boolean playerCenter) {
        super(fov);
        dist = 100f;
        pos.y = dist;
        this.playerCenter = playerCenter;
        if (enableMapEditor) this.playerCenter = false;
    }

    @Override
    public void pan(float dx, float dy) {
        pos.add(dx, 0, -dy);
    }


    public void update(float delta) {

        if (playerCenter) {
            camera.position.set(player.pos.x, pos.y, player.pos.z);
        } else {
            camera.position.set(pos);
        }

        camera.view.setFromEulerAngles(yaw, pitch, 0);
        camera.direction.set(0f, -1f, 0f);
        camera.up.set(0f, 0f, -1f);

        camera.update();
    }

    @Override
    public void rotate(float dx, float dy) {

    }

    @Override
    public void zoom(float y) {
        pos.y = MathUtils.clamp(MathUtils.lerp(pos.y, pos.y + y * 10, 0.5f), 10, 200);
    }
}
