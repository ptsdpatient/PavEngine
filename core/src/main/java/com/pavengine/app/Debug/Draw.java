package com.pavengine.app.Debug;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavScreen.GameWorld.shapeRenderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.pavengine.app.Cell;
import com.pavengine.app.CellType;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavRay;
import com.pavengine.app.SlopeRay;

public class Draw {

    public static void debugRing(Vector3[] rings) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.CYAN);
        for (int i = 0; i < rings.length; i++) {
            Vector3 start = rings[i];
            Vector3 end = rings[(i + 1) % rings.length];
            shapeRenderer.line(start, end);
        }
        shapeRenderer.end();
    }

    public static void debugCube(PavBounds box, Color color) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);

        Vector3[] corners = box.getVertices();

        shapeRenderer.line(corners[0], corners[1]);
        shapeRenderer.line(corners[0], corners[2]);
        shapeRenderer.line(corners[0], corners[4]);

        shapeRenderer.line(corners[1], corners[3]);
        shapeRenderer.line(corners[1], corners[5]);

        shapeRenderer.line(corners[2], corners[3]);
        shapeRenderer.line(corners[2], corners[6]);

        shapeRenderer.line(corners[3], corners[7]);

        shapeRenderer.line(corners[4], corners[5]);
        shapeRenderer.line(corners[4], corners[6]);

        shapeRenderer.line(corners[5], corners[7]);
        shapeRenderer.line(corners[6], corners[7]);
        shapeRenderer.end();
    }

    public static void debugRectangle(Rectangle rect, Color color) {
        shapeRenderer.setProjectionMatrix(overlayCamera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        shapeRenderer.end();
    }

    public static void debugRay(PavRay ray) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        Vector3 end = new Vector3(ray.ray.direction).scl(ray.distance).add(ray.ray.origin);
        shapeRenderer.line(ray.ray.origin, end);
        shapeRenderer.end();
    }

    public static void debugRay(SlopeRay ray) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.line(ray.ray.origin, ray.intersection);
        shapeRenderer.end();
    }

    public static void debugRay(Ray ray) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        Vector3 end = new Vector3(ray.direction).scl(20).add(ray.origin);
        shapeRenderer.line(ray.origin, end);
        shapeRenderer.end();
    }

    public static void debugLine(Vector3 start, Vector3 end) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.line(start, end);
        shapeRenderer.end();
    }

    public static void debugLine(Vector3 start, Vector3 end,Color color) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);
        Gdx.gl.glDisable(GL20.GL_BLEND);

        shapeRenderer.setColor(color);
        shapeRenderer.line(start, end);
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glEnable(GL20.GL_BLEND);
    }

    public static void debugCell(Cell obj) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(obj.debugColor);

        if (obj.type == CellType.EXPLORED) shapeRenderer.setColor(Color.CYAN);

        if (obj.isStart || obj.type == CellType.ROAD) shapeRenderer.setColor(Color.GREEN);
        if (obj.isEnd) shapeRenderer.setColor(Color.BLUE);

        Vector3 min = new Vector3();
        Vector3 max = new Vector3();
        obj.bounds.getMin(min);
        obj.bounds.getMax(max);
        Vector3[] corners = new Vector3[8];
        corners[0] = new Vector3(min.x, min.y, min.z);
        corners[1] = new Vector3(min.x, min.y, max.z);
        corners[2] = new Vector3(min.x, max.y, min.z);
        corners[3] = new Vector3(min.x, max.y, max.z);
        corners[4] = new Vector3(max.x, min.y, min.z);
        corners[5] = new Vector3(max.x, min.y, max.z);
        corners[6] = new Vector3(max.x, max.y, min.z);
        corners[7] = new Vector3(max.x, max.y, max.z);

        shapeRenderer.line(corners[0], corners[1]);
        shapeRenderer.line(corners[0], corners[2]);
        shapeRenderer.line(corners[0], corners[4]);

        shapeRenderer.line(corners[1], corners[3]);
        shapeRenderer.line(corners[1], corners[5]);

        shapeRenderer.line(corners[2], corners[3]);
        shapeRenderer.line(corners[2], corners[6]);

        shapeRenderer.line(corners[3], corners[7]);

        shapeRenderer.line(corners[4], corners[5]);
        shapeRenderer.line(corners[4], corners[6]);

        shapeRenderer.line(corners[5], corners[7]);
        shapeRenderer.line(corners[6], corners[7]);

        shapeRenderer.end();

    }


    public static void debugCube(PavBounds box) {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);

        Vector3[] corners = box.getVertices();

        shapeRenderer.line(corners[0], corners[1]);
        shapeRenderer.line(corners[0], corners[2]);
        shapeRenderer.line(corners[0], corners[4]);

        shapeRenderer.line(corners[1], corners[3]);
        shapeRenderer.line(corners[1], corners[5]);

        shapeRenderer.line(corners[2], corners[3]);
        shapeRenderer.line(corners[2], corners[6]);

        shapeRenderer.line(corners[3], corners[7]);

        shapeRenderer.line(corners[4], corners[5]);
        shapeRenderer.line(corners[4], corners[6]);

        shapeRenderer.line(corners[5], corners[7]);
        shapeRenderer.line(corners[6], corners[7]);
        shapeRenderer.end();
    }
}
