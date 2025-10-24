package com.pavengine.app.PavPlayer;

import static com.pavengine.app.Methods.isKeyJustPressed;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.gamePause;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavScreen.GameScreen.lanes;
import static com.pavengine.app.PavScreen.GameWorld.cameraBehavior;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.pavengine.app.CameraBehaviorType;
import com.pavengine.app.PavBounds.Entrance;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavGameObject.GameObject;

public class Movement implements PlayerBehavior {
    final float walkSpeed = 6f;
    final float runSpeed = 12f;
    final float accel = 10f;
    final float decel = 12f;
    final Vector3 flatForward = new Vector3();
    final Vector3 flatRight = new Vector3();
    final Vector3 moveDir = new Vector3();
    public Vector3 futurePos = new Vector3();
    float speed = 0f, targetSpeed = 0f, slopeFactor = 0f;

    public boolean isColliding(GameObject player, Vector3 nextPos) {
        for (GameObject obj : targetObjects) {
            if (obj == player) continue;

            if (
                player.box.ringOverlaps(obj.box, nextPos) &&
                    // player get height / 2f is a way to keep the player pos vector to bottom most for player bottom center position
                    (player.pos.y - player.getHeight() / 2f <= obj.pos.y + obj.getHeight())
            ) {

                return true;
            }
        }

        for (GameObject obj : staticObjects) {

            if (obj == player) continue;

            if (obj.isRoom) {
                for (PavBounds bounds : obj.walls) {

                    if (
                        !bounds.isGround &&
                            player.box.ringOverlaps(bounds,nextPos) &&
                            // player get height / 2f is a way to keep the player pos vector to bottom most for player bottom center position
                            (player.pos.y - player.getHeight() / 2f <= obj.pos.y + obj.getHeight())
                    ) {
                        if(!obj.entrances.isEmpty()){
                            for(Entrance e : obj.entrances) {
                                if(e.bounds.containsRing(player.box.rings,nextPos)) {
                                    print("contains");
                                    return false;
                                }
                            }
                        }
                        return true;
                    }
                }
            } else {
                if (
                    player.box.ringOverlaps(obj.box, nextPos) &&
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
    public void update(GameObject player, float delta) {
        moveDir.setZero();


        if(cameraBehavior != CameraBehaviorType.TopDown) {
            flatForward.set(camera.direction.x, 0, camera.direction.z).nor();
            flatRight.set(flatForward).crs(camera.up).nor();

            if (Gdx.input.isKeyPressed(Input.Keys.W))
                moveDir.add(flatForward);
            if (Gdx.input.isKeyPressed(Input.Keys.S))
                moveDir.sub(flatForward);
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                moveDir.add(flatRight);
            if (Gdx.input.isKeyPressed(Input.Keys.A))
                moveDir.sub(flatRight);
        } else {

            if (Gdx.input.isKeyPressed(Input.Keys.W))
                moveDir.add(0, 0, -1);   // +Z
            if (Gdx.input.isKeyPressed(Input.Keys.S))
                moveDir.add(0, 0, 1);  // -Z
            if (Gdx.input.isKeyPressed(Input.Keys.D))
                moveDir.add(1, 0, 0);   // +X
            if (Gdx.input.isKeyPressed(Input.Keys.A))
                moveDir.add(-1, 0, 0);  // -X
        }


        if (!moveDir.isZero()) {
            moveDir.nor();

            targetSpeed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? runSpeed : walkSpeed;

            slopeFactor = moveDir.dot(player.slopeNormal);

            targetSpeed *= 1f
                + 0.5f * Math.max(0, slopeFactor)
                - 0.25f * Math.max(0, -slopeFactor);

            speed = MathUtils.lerp(speed, targetSpeed, accel * delta);

            futurePos.set(player.pos).mulAdd(moveDir, speed * delta);

            if (!isColliding(player, futurePos)) {
                player.pos.set(futurePos);
            } else {
                speed = 0f;
            }

        } else {
            speed = MathUtils.lerp(speed, 0f, decel * delta);
        }
    }


}
