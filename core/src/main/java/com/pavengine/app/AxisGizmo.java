package com.pavengine.app;

import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.pavCamera;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameWorld.shapeRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class AxisGizmo {
    private OrthographicCamera orthoCam;
    private Rectangle xRect, yRect, zRect;
    private float centerX, centerY, axisLength;
    private Vector3 xDir = new Vector3(1, 0, 0);
    private Vector3 yDir = new Vector3(0, 1, 0);
    private Vector3 zDir = new Vector3(0, 0, 1);

    public AxisGizmo(OrthographicCamera orthoCam) {
        this.orthoCam = orthoCam;
        axisLength = 50f;

        xRect = new Rectangle();
        yRect = new Rectangle();
        zRect = new Rectangle();
    }


    public void update() {
        centerX = orthoCam.viewportWidth - 80;
        centerY = 80;

        Matrix4 rot = new Matrix4();
        rot.setToLookAt(camera.direction, camera.up);

        xDir.set(1, 0, 0).rot(rot).nor();
        yDir.set(0, 1, 0).rot(rot).nor();
        zDir.set(0, 0, 1).rot(rot).nor();
    }

    public void draw() {

        debugRectangle(xRect,true,Color.RED);
        debugRectangle(yRect,true,Color.GREEN);
        debugRectangle(zRect,true,Color.BLUE);

        shapeRenderer.setProjectionMatrix(orthoCam.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(centerX, centerY,
            centerX + xDir.x * axisLength,
            centerY + xDir.y * axisLength);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(centerX, centerY,
            centerX + yDir.x * axisLength,
            centerY + yDir.y * axisLength);

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.line(centerX, centerY,
            centerX + zDir.x * axisLength,
            centerY + zDir.y * axisLength);

        shapeRenderer.end();

        xRect.set(centerX + xDir.x * axisLength - 10, centerY + xDir.y * axisLength - 10, 20, 20);
        yRect.set(centerX + yDir.x * axisLength - 10, centerY + yDir.y * axisLength - 10, 20, 20);
        zRect.set(centerX + zDir.x * axisLength - 10, centerY + zDir.y * axisLength - 10, 20, 20);
    }

    public void handleInput() {
        if (Gdx.input.justTouched()) {
            if (cursor.clicked(xRect)) {
                System.out.println("X");
                lookFromAxis(Vector3.X);

            } else if (cursor.clicked(yRect)) {
                System.out.println("Y");
                lookFromAxis(Vector3.Y);

            } else if (cursor.clicked(zRect)) {
                System.out.println("Z");
                lookFromAxis(Vector3.Z);
            }
        }
    }

    private void lookFromAxis(Vector3 axis) {
        float distance = 10f;
//        Vector3 pos = selectedObject==null? new Vector3(0,0,0) : selectedObject.pos;
//
//        Vector3 newPos = new Vector3(pos).add(new Vector3(axis).scl(distance));

//        camera.position.set(newPos);
//        camera.lookAt(pos);

        camera.position.set(new Vector3(10,0,0));
//        camera.direction.set(new Vector3(-1,0,0).nor());
//        camera.update();
        pavCamera.setDirection(new Vector3(-1, 0, 0));

    }


}
