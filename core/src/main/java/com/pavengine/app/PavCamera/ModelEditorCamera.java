package com.pavengine.app.PavCamera;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;

public class ModelEditorCamera extends PavCamera{

    public PerspectiveCamera camera;
    private float yaw = 0f;
    private float pitch = 0f;
    private float moveSpeed = 20f;
    private float mouseSensitivity = 0.2f;

    public ModelEditorCamera(float fov, float viewportWidth, float viewportHeight) {
        camera = new PerspectiveCamera(fov, viewportWidth, viewportHeight);
        camera.position.set(0, 5, 10);
        camera.lookAt(0, 5, 0);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();
    }


    public void update(float delta) {
        handleInput(delta);
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

    private void handleInput(float delta) {
        float move = moveSpeed * delta;

        // Mouse look
        if (Gdx.input.isTouched()) {
            float deltaX = -Gdx.input.getDeltaX() * mouseSensitivity;
            float deltaY = -Gdx.input.getDeltaY() * mouseSensitivity;
            yaw += deltaX;
            pitch = MathUtils.clamp(pitch + deltaY, -89f, 89f);
        }

        // Calculate direction
        Vector3 direction = new Vector3(
            MathUtils.sinDeg(yaw) * MathUtils.cosDeg(pitch),
            MathUtils.sinDeg(pitch),
            MathUtils.cosDeg(yaw) * MathUtils.cosDeg(pitch)
        ).nor();

        Vector3 right = new Vector3(direction).crs(Vector3.Y).nor();
        Vector3 up = new Vector3(right).crs(direction).nor();

        // Keyboard movement
        if (Gdx.input.isKeyPressed(Input.Keys.W)) camera.position.add(direction.scl(move));
        if (Gdx.input.isKeyPressed(Input.Keys.S)) camera.position.sub(direction.scl(move));
        if (Gdx.input.isKeyPressed(Input.Keys.A)) camera.position.sub(right.scl(move));
        if (Gdx.input.isKeyPressed(Input.Keys.D)) camera.position.add(right.scl(move));
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) camera.position.add(up.scl(move));
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) camera.position.sub(up.scl(move));

        camera.direction.set(direction);
        camera.update();
    }
}
