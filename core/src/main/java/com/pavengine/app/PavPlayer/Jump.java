package com.pavengine.app.PavPlayer;

import static com.pavengine.app.Debug.Draw.debugCube;
import static com.pavengine.app.PavPlayer.PavPlayer.player;
import static com.pavengine.app.PavScreen.GameWorld.groundObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.pavengine.app.PavGameObject.GameObject;

public class Jump implements PlayerBehavior {

    boolean jumping = true;
    float velocityY = 0f;
    float jumpStrength = 18f;
    float gravity = -20f;
    float bounceFactor = 0.6f;

    public boolean isColliding() {
        for (GameObject obj : targetObjects) {
            if (player.footBox.intersects(obj.box)) {
                return true;
            }
        }

        for (GameObject obj : groundObjects) {
            if (player.footBox.intersects(obj.box)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(GameObject player, float delta) {
        debugCube(player.footBox);
        if (jumping) {
            velocityY += gravity * delta;
            player.pos.y = MathUtils.lerp(player.pos.y, player.pos.y + velocityY * delta, 1f);

            for (GameObject obj : targetObjects) {
                if (obj.box.intersects(player.footBox) && velocityY <= 0) {

                    if (Math.abs(velocityY) > 1f) {
                        if (Math.abs(velocityY) > 7f) player.playAnimation(0, false, false);
                        velocityY = -velocityY * bounceFactor;
                    } else {
                        velocityY = 0f;
                        jumping = false;
                    }
                }
            }
            for (GameObject obj : groundObjects) {
                if (obj.box.intersects(player.footBox) && velocityY <= 0) {

                    if (Math.abs(velocityY) > 1f) {
                        if (Math.abs(velocityY) > 7f) player.playAnimation(0, false, false);
                        velocityY = -velocityY * bounceFactor;
                    } else {
                        velocityY = 0f;
                        jumping = false;
                    }
                }
            }

        } else if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            player.playAnimation(0, false, false);
            jumping = true;
            velocityY = jumpStrength;
        } else if (!isColliding()) {
            jumping = true;

        } else {
            for (GameObject obj : groundObjects) {
                if (player.footBox.intersects(obj.box) && player.box.intersects(obj.box)) {
                    velocityY = 0f;
                    player.pos.y = MathUtils.lerp(player.pos.y, (
                        player.slopeRays.get(0).intersection.y +
                            player.slopeRays.get(1).intersection.y +
                            player.slopeRays.get(2).intersection.y
                    ) / 3f + player.getHeight() * 0.5f, 0.1f);
                }
            }
        }
    }
}
