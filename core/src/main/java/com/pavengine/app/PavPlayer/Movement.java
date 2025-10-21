package com.pavengine.app.PavPlayer;

import static com.pavengine.app.PavEngine.gamePause;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavScreen.GameScreen.lanes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
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
    int currentLane = 1, targetLane = 0;
    float t = 1f, duration = 0.3f;
    Vector3 from = new Vector3(), to = new Vector3();


    @Override
    public void update(GameObject player, float delta) {
        moveDir.setZero();
        if (!gamePause) {

            if (t >= 1f) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.A) && currentLane > 0) {
                    soundBox.playSound("rail_move.wav");
                    targetLane = currentLane - 1;
                    from.set(player.pos);
                    to.set(lanes.get(targetLane).end);
                    t = 0f;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.D) && currentLane < lanes.size - 1) {
                    soundBox.playSound("rail_move.wav");
                    targetLane = currentLane + 1;
                    from.set(player.pos);
                    to.set(lanes.get(targetLane).end);
                    t = 0f;
                }
            } else {
                t = Math.min(t + delta / duration, 1f);
                float smooth = t * t * (3 - 2 * t);
                player.pos.set(from).lerp(to, smooth);
                if (t >= 1f) currentLane = targetLane;
            }
        }

//        if(isKeyJustPressed("A")) {
//            if(currentLane>0){
//                currentLane--;
//                print(currentLane);
//                player.pos.set(lanes.get(currentLane).end.cpy());
//            }
//        }
//        if(isKeyJustPressed("D")) {
//            if(currentLane<lanes.size-1){
//                currentLane++;
//                print(currentLane);
//                player.pos.set(lanes.get(currentLane).end.cpy());
//            }
//        }

//        if(cameraBehavior != CameraBehaviorType.TopDown) {
//            flatForward.set(camera.direction.x, 0, camera.direction.z).nor();
//            flatRight.set(flatForward).crs(camera.up).nor();
//
//            if (Gdx.input.isKeyPressed(Input.Keys.W))
//                moveDir.add(flatForward);
//            if (Gdx.input.isKeyPressed(Input.Keys.S))
//                moveDir.sub(flatForward);
//            if (Gdx.input.isKeyPressed(Input.Keys.D))
//                moveDir.add(flatRight);
//            if (Gdx.input.isKeyPressed(Input.Keys.A))
//                moveDir.sub(flatRight);
//        } else {
//
//
//            if (Gdx.input.isKeyPressed(Input.Keys.W))
//                moveDir.add(0, 0, -1);   // +Z
//            if (Gdx.input.isKeyPressed(Input.Keys.S))
//                moveDir.add(0, 0, 1);  // -Z
//            if (Gdx.input.isKeyPressed(Input.Keys.D))
//                moveDir.add(1, 0, 0);   // +X
//            if (Gdx.input.isKeyPressed(Input.Keys.A))
//                moveDir.add(-1, 0, 0);  // -X
//        }
//
//
//        if (!moveDir.isZero()) {
//            moveDir.nor();
//
//            targetSpeed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? runSpeed : walkSpeed;
//
//            slopeFactor = moveDir.dot(player.slopeNormal);
//
//            targetSpeed *= 1f
//                + 0.5f * Math.max(0, slopeFactor)
//                - 0.25f * Math.max(0, -slopeFactor);
//
//            speed = MathUtils.lerp(speed, targetSpeed, accel * delta);
//
//            futurePos.set(player.pos).mulAdd(moveDir, speed * delta);
//
////            if (!isColliding(player, futurePos)) {
////                player.pos.set(futurePos);
////            } else {
////                speed = 0f;
////            }
//        } else {
//            speed = MathUtils.lerp(speed, 0f, decel * delta);
//        }
    }


}
