package com.pavengine.app.PavCamera;


import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;


public class BoundsEditorCamera extends PavCamera {
    private final Vector3 target = new Vector3(0, 0, 0); // focus/orbit point
    private float distance = 10f; // distance from target
    private float sensitivity = 0.3f;
    private float zoomSpeed = 10f;
    private float panSpeed = 0.01f;

    public BoundsEditorCamera(float fov) {
        super(fov);
        updateCameraPosition();
    }

    @Override
    public void update(float delta) {
        handleInput(delta);
    }

    private void handleInput(float delta) {
        float dx = -Gdx.input.getDeltaX() * sensitivity;
        float dy = -Gdx.input.getDeltaY() * sensitivity;

        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            yaw += dx;
            pitch = MathUtils.clamp(pitch - dy, -89, 89);
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            Vector3 right = new Vector3(MathUtils.sinDeg(yaw - 90), 0, MathUtils.cosDeg(yaw - 90));
            Vector3 up = Vector3.Y.cpy();

            target.mulAdd(right, -dx * panSpeed * distance);
            target.mulAdd(up, -dy * panSpeed * distance);
        }

        updateCameraPosition();
    }

    private void updateCameraPosition() {
        float cosPitch = MathUtils.cosDeg(pitch);
        float sinPitch = MathUtils.sinDeg(pitch);
        float cosYaw = MathUtils.cosDeg(yaw);
        float sinYaw = MathUtils.sinDeg(yaw);

        camera.position.set(
            target.x + distance * cosPitch * sinYaw,
            target.y + distance * sinPitch,
            target.z + distance * cosPitch * cosYaw
        );

        camera.lookAt(target);
        camera.up.set(Vector3.Y);
        camera.update();
    }

    @Override
    public void rotate(float dx, float dy) { /* not used */ }

    @Override
    public void pan(float dx, float dy) { /* handled above */ }

    @Override
    public void zoom(float y) {
        distance *= 1f + y * zoomSpeed * Gdx.graphics.getDeltaTime();
        distance = MathUtils.clamp(distance, 0.1f, 200f);
    }
}
