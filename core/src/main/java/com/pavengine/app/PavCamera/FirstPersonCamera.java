package com.pavengine.app.PavCamera;

import static com.pavengine.app.Methods.isButtonPressed;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.blastRadius;
import static com.pavengine.app.PavEngine.gamePause;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.playerDamage;
import static com.pavengine.app.PavEngine.playerDamageRate;
import static com.pavengine.app.PavEngine.playerDamageTime;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavEngine.sprayCooldown;
import static com.pavengine.app.PavEngine.sprayLimit;
import static com.pavengine.app.PavEngine.sprayTime;
import static com.pavengine.app.PavPlayer.PavPlayer.player;
import static com.pavengine.app.PavScreen.GameScreen.muzzleFlash;
import static com.pavengine.app.PavScreen.GameScreen.playerRay;
import static com.pavengine.app.PavScreen.GameScreen.robots;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.pavengine.app.PavScript.Enemies.Enemy;

// ==================== First Person ====================
public class FirstPersonCamera extends PavCamera {

    private Vector3 recoilRotation = new Vector3(0, 0, 0); // x=pitch, y=yaw, z unused
    private float recoilRecovery = 10f;
    public FirstPersonCamera(float fov) {
        super(fov);
    }

    public Quaternion getCameraRotation() {
        float yawOffset = 90f; // change this if you need to fix yaw alignment
        float yawRad = (float) Math.toRadians(yaw + yawOffset);
        float pitchRad = (float) Math.toRadians(pitch);

        // combine yaw and pitch into one quaternion
        return new Quaternion()
            .setEulerAnglesRad(yawRad, -pitchRad, 0);
    }

    public void rotate(float dx, float dy) {
        pitch = MathUtils.clamp(pitch - dy * 0.1f, -10f, 60f);
    }


    public void update(float delta) {

        boolean firing = isButtonPressed("LEFT");

        if (firing) {
            if (sprayCooldown <= 0f) {
                sprayTime += delta;
                playerDamageTime += delta;

                if (sprayTime <= sprayLimit) {

                    if (playerDamageTime >= playerDamageRate) {
                        playerDamageTime = 0f;

                        // Deal damage to nearby enemies
                        for (Enemy obj : robots) {
                            if (playerRay.intersection.dst(obj.getObject().center) < blastRadius) {
                                float damageApplied = (playerDamage * (obj.getObject().center.dst(camera.position) < 7 ? 2 : 1));
                                obj.health -= damageApplied;
                                print(damageApplied);
                                print(obj.health);
                            }
                        }

                        // Spawn muzzle flash on overlay camera
                        Vector3 muzzleWorld = new Vector3(playerRay.ray.origin)
                            .mulAdd(playerRay.ray.direction, 1f);
                        Vector3 screenPos = camera.project(muzzleWorld);
                        float effectX = screenPos.x / Gdx.graphics.getWidth() * overlayCamera.viewportWidth;
                        float effectY = screenPos.y / Gdx.graphics.getHeight() * overlayCamera.viewportHeight;

                        muzzleFlash.spawn(effectX, effectY, 7);

                        soundBox.playSound("turret_2.wav");

                        // --- Gradual recoil (slow start, ease-in) ---
                        float recoilFactor = MathUtils.clamp(sprayTime / sprayLimit, 0f, 1f);
                        recoilFactor = recoilFactor * recoilFactor; // ease-in: starts slow, grows faster

                        recoilRotation.x += MathUtils.random(0.5f, 1.5f) * (1f + 2f * recoilFactor); // pitch up
                        recoilRotation.y += MathUtils.random(-0.5f, 0.5f) * (1f + 2f * recoilFactor); // yaw
                    }

                } else {
                    sprayCooldown = 5f; // reset cooldown if spray limit exceeded
                }

            } else sprayCooldown -= delta; // reduce cooldown timer

        } else {
            sprayTime = 0f; // reset spray timer if not firing
        }

        // --- Smooth recoil recovery ---
        recoilRotation.x = MathUtils.lerp(recoilRotation.x, 0f, recoilRecovery * delta);
        recoilRotation.y = MathUtils.lerp(recoilRotation.y, 0f, recoilRecovery * delta);

        // --- Calculate camera direction with recoil applied ---
        float finalPitch = pitch - recoilRotation.x;
        float finalYaw = yaw - recoilRotation.y;

        Vector3 dir = new Vector3(
            (float) (Math.cos(Math.toRadians(finalPitch)) * Math.cos(Math.toRadians(finalYaw))),
            (float) (Math.sin(Math.toRadians(finalPitch))),
            (float) (Math.cos(Math.toRadians(finalPitch)) * Math.sin(Math.toRadians(finalYaw)))
        ).nor();

        if (!gamePause) {
            player.rotation.set(getCameraRotation());
            camera.direction.set(dir);
        }

        camera.position.set(player.pos.x, player.pos.y + 2, player.pos.z);
        camera.up.set(Vector3.Y);
        camera.update();

    }


    @Override
    public void pan(float dx, float dy) {

    }

    @Override
    public void zoom(float y) {

    }

}
