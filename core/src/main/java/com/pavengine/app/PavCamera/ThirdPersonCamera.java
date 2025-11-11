package com.pavengine.app.PavCamera;


import static com.pavengine.app.Methods.getEulerAngles;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavPlayer.PavPlayer.player;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavGameObject.GameObject;

public class ThirdPersonCamera extends PavCamera {


    public ThirdPersonCamera(float fov) {
        super(fov);
        dist = 10f;
        yaw = 0f;
        pitch = 25f;
        print("third ps");
    }



    public void getCameraRotation(Quaternion rotation) {
        Vector3 dir = new Vector3(camera.direction).set(camera.direction.x, 0, camera.direction.z).nor();

        float yawRad = (float) Math.atan2(dir.x, dir.z);

        rotation.setEulerAngles(yawRad * MathUtils.radiansToDegrees, rotation.getPitch(),rotation.getRoll());
    }


    public void rotate(float dx, float dy) {
        // yaw is the horizontal sensitivity
        yaw += dx * 0.3f;
        // pitch value is vertical sensitivity and min and max are vertical limits
        pitch = MathUtils.clamp(pitch - dy * 0.1f, 5f, 50f);
    }

    public boolean isColliding(GameObject player, Quaternion nextRot) {
        for (GameObject obj : targetObjects) {
            if (obj == player) continue;
            for(PavBounds box : obj.boxes) {
                if (
                    new OrientedBoundingBox(player.bounds, new Matrix4(player.pos, nextRot, player.size)).intersects(box.box) &&
                        // player get height / 2f is a way to keep the player pos vector to bottom most for player bottom center position
                        (player.pos.y - player.getHeight() / 2f <= obj.pos.y + obj.getHeight())
                ) {
                    return true;
                }
            }
        }


        return false;
    }

    @Override
    public void pan(float dx, float dy) {

    }

    public void update(float delta) {

        getCameraRotation(player.rotation);

        camera.position.lerp(
            new Vector3(
                player.pos.x - (float) (dist * Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw))),
                player.pos.y + (float) (dist * Math.sin(Math.toRadians(pitch))),
                player.pos.z - (float) (dist * Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw)))
            )
            , 5f * delta);

        camera.lookAt(player.pos);
        camera.up.set(Vector3.Y);
        camera.update();
    }

    @Override
    public void zoom(float y) {

    }

}
