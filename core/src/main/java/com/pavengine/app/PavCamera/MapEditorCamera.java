package com.pavengine.app.PavCamera;


import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
public class MapEditorCamera extends PavCamera {
    private float moveSpeed = 20f;
    private float mouseSensitivity = 0.2f;
    private final Vector3 direction = new Vector3();
    private final Vector3 right = new Vector3();
    private final Vector3 up = new Vector3();
    float move = 0, acc = 0, deltaX = 0, deltaY = 0;

    public MapEditorCamera(float fov) {
        super(fov);

        camera.position.set(6, 6, 6);
        camera.direction.set(-0.6376863f, -0.41910368f, -0.6463035f).nor();

        yaw = MathUtils.atan2(camera.direction.x, camera.direction.z) * MathUtils.radiansToDegrees;
        pitch = MathUtils.asin(camera.direction.y) * MathUtils.radiansToDegrees;

        camera.update();
    }

    @Override
    public void update(float delta) {
        handleInput(delta);
    }

    private void handleInput(float delta) {

        acc = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 3 : 1;
        move = moveSpeed * delta * acc;

        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            deltaX = -Gdx.input.getDeltaX() * mouseSensitivity;
            deltaY = -Gdx.input.getDeltaY() * mouseSensitivity;
            yaw += deltaX;
            pitch = MathUtils.clamp(
                pitch + deltaY,
                -89f,
                89f
            );
        }

        direction.set(
            MathUtils.sinDeg(yaw) * MathUtils.cosDeg(pitch),
            MathUtils.sinDeg(pitch),
            MathUtils.cosDeg(yaw) * MathUtils.cosDeg(pitch)
        ).nor();

        right.set(direction).crs(Vector3.Y).nor();
        up.set(right).crs(direction).nor();

        if (Gdx.input.isKeyPressed(Input.Keys.W))
            camera.position.mulAdd(direction, move);
        if (Gdx.input.isKeyPressed(Input.Keys.S))
            camera.position.mulAdd(direction, -move);
        if (Gdx.input.isKeyPressed(Input.Keys.A))
            camera.position.mulAdd(right, -move);
        if (Gdx.input.isKeyPressed(Input.Keys.D))
            camera.position.mulAdd(right, move);
        if (Gdx.input.isKeyPressed(Input.Keys.SPACE))
            camera.position.mulAdd(up, move);
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT))
            camera.position.mulAdd(up, -move);

        camera.direction.set(direction);
        camera.update();

    }

    @Override
    public void rotate(float dx, float dy) { }

    @Override
    public void pan(float dx, float dy) { }

    @Override
    public void zoom(float y) { }
}
