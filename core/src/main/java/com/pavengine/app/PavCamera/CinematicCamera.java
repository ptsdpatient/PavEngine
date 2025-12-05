package com.pavengine.app.PavCamera;


import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;
import static com.pavengine.app.PavScreen.CinematicEditor.cameraReferenceLayout;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;
import static com.pavengine.app.PavScreen.CinematicEditor.playingScene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;

public class CinematicCamera extends PavCamera {
    private float moveSpeed = 20f;
    private float mouseSensitivity = 0.2f;
    private final Vector3 direction = new Vector3();
    private final Vector3 right = new Vector3();
    private final Vector3 up = new Vector3();
    float move = 0, acc = 0, deltaX = 0, deltaY = 0;
    private final StringBuilder camInfo = new StringBuilder();

    public CinematicCamera(float fov) {
        super(fov);

        camera.position.set(6, 6, 6);
        camera.direction.set(-0.6376863f, -0.41910368f, -0.6463035f).nor();

        print("new camera");

        yaw = MathUtils.atan2(camera.direction.x, camera.direction.z) * MathUtils.radiansToDegrees;
        pitch = MathUtils.asin(camera.direction.y) * MathUtils.radiansToDegrees;

        camera.update();
    }


    @Override
    public void update(float delta) {
        if((Gdx.input.getInputProcessor() == cinematicEditorInput && !playingScene)) {
            handleInput(delta);
        }
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
            if(!cursor.clicked(cinematicTimeline.bounds))
                camera.position.mulAdd(up, -move);

        camera.direction.set(direction);
        camera.update();

        setReferenceDetail();
    }


    private void setReferenceDetail() {
        camInfo.setLength(0);

        camInfo.append("pos: ");
        appendVec3Inline(camera.position);

        camInfo.append("\ndir: ");
        appendVec3Inline(camera.direction);

        cameraReferenceLayout.setText(
            gameFont[1],
            camInfo.toString(),
            Color.WHITE,
            500,
            Align.left,
            true
        );
    }

    private void appendVec3Inline(Vector3 v) {
        appendAlignedInline(v.x);
        appendAlignedInline(v.y);
        appendAlignedInline(v.z);
    }

    private void appendAlignedInline(float value) {
        value = ((int) (value * 100)) / 100f; // round to 2 decimals (no reflection)
        String s = Float.toString(value);

        if (!s.contains(".")) s += ".0";           // ensure at least 1 decimal
        if (s.indexOf('.') == s.length() - 2) s += "0"; // ensure exactly 2 decimals

        while (s.length() < 7) s = " " + s;        // pad for alignment (fixed width)

        camInfo.append(s).append(" ");            // inline separation
    }


    @Override
    public void rotate(float dx, float dy) { }

    @Override
    public void pan(float dx, float dy) { }

    @Override
    public void zoom(float y) { }
}
