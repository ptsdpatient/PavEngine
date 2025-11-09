package com.pavengine.app;

import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavScreen.GameWorld.shapeRenderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.badlogic.gdx.utils.Array;

public class AxisGizmo3D {
    public class GizmoCube {
        public OrientedBoundingBox box = new OrientedBoundingBox();
        public Vector3 direction;
        public GizmoCube(Vector3 direction) {
            this.direction = direction;
        }

        public void set(BoundingBox unitBox, Matrix4 tmpMat) {
            box.set(unitBox,tmpMat);
        }
    }
    private static final float AXIS_LENGTH = 4f;
    private float BOX_THICKNESS = 0.4f; // changeable thickness!

    private final Vector3 origin = new Vector3();
    private final Vector3 xEnd = new Vector3();
    private final Vector3 yEnd = new Vector3();
    private final Vector3 zEnd = new Vector3();

    public final Matrix4 transform = new Matrix4();
    public final Array<GizmoCube> boxes = new Array<>();

    private final Matrix4 tmpMat = new Matrix4();
    private final Vector3 tmpCenter = new Vector3();

    private static final BoundingBox UNIT_BOX = new BoundingBox(
        new Vector3(-0.5f, -0.5f, -0.5f),
        new Vector3(0.5f, 0.5f, 0.5f)
    );

    Vector3[] gizmoDirections = new Vector3[] {
        Vector3.X, Vector3.Y, Vector3.Z
    };

    public AxisGizmo3D() {
        for(Vector3 direction : gizmoDirections) {
            boxes.add(new GizmoCube(direction));
        }
    }

    public void setPosition(Vector3 origin) {
        this.origin.set(origin);
    }

    public void setThickness(float thickness) {
        this.BOX_THICKNESS = thickness;
    }

    public void update() {
        xEnd.set(origin).add(AXIS_LENGTH, 0, 0);
        yEnd.set(origin).add(0, AXIS_LENGTH, 0);
        zEnd.set(origin).add(0, 0, AXIS_LENGTH);

        // X box
        tmpCenter.set(AXIS_LENGTH / 2f, 0, 0).add(origin);
        tmpMat.idt()
            .translate(tmpCenter)
            .scale(AXIS_LENGTH, BOX_THICKNESS, BOX_THICKNESS);
        boxes.get(0).set(UNIT_BOX, tmpMat);

        // Y box
        tmpCenter.set(0, AXIS_LENGTH / 2f, 0).add(origin);
        tmpMat.idt()
            .translate(tmpCenter)
            .scale(BOX_THICKNESS, AXIS_LENGTH, BOX_THICKNESS);
        boxes.get(1).set(UNIT_BOX, tmpMat);

        // Z box
        tmpCenter.set(0, 0, AXIS_LENGTH / 2f).add(origin);
        tmpMat.idt()
            .translate(tmpCenter)
            .scale(BOX_THICKNESS, BOX_THICKNESS, AXIS_LENGTH);
        boxes.get(2).set(UNIT_BOX, tmpMat);
    }

    public void draw() {
        shapeRenderer.setProjectionMatrix(camera.combined);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.line(origin, xEnd);

        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(origin, yEnd);

        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.line(origin, zEnd);

        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        drawCube(shapeRenderer, xEnd, Color.RED);
        drawCube(shapeRenderer, yEnd, Color.GREEN);
        drawCube(shapeRenderer, zEnd, Color.BLUE);
        shapeRenderer.end();
    }

    private void drawCube(ShapeRenderer sr, Vector3 center, Color color) {
        float h = BOX_THICKNESS; // use thickness dynamically!

        Vector3 p000 = new Vector3(center.x - h, center.y - h, center.z - h);
        Vector3 p001 = new Vector3(center.x - h, center.y - h, center.z + h);
        Vector3 p010 = new Vector3(center.x - h, center.y + h, center.z - h);
        Vector3 p011 = new Vector3(center.x - h, center.y + h, center.z + h);
        Vector3 p100 = new Vector3(center.x + h, center.y - h, center.z - h);
        Vector3 p101 = new Vector3(center.x + h, center.y - h, center.z + h);
        Vector3 p110 = new Vector3(center.x + h, center.y + h, center.z - h);
        Vector3 p111 = new Vector3(center.x + h, center.y + h, center.z + h);

        sr.setColor(color);

        sr.line(p000, p001); sr.line(p001, p011); sr.line(p011, p010); sr.line(p010, p000);
        sr.line(p100, p101); sr.line(p101, p111); sr.line(p111, p110); sr.line(p110, p100);
        sr.line(p000, p100); sr.line(p001, p101); sr.line(p010, p110); sr.line(p011, p111);
    }
}


