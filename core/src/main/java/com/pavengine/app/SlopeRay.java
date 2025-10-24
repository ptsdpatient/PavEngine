package com.pavengine.app;

import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.pavengine.app.PavGameObject.GameObject;

public class SlopeRay {
    public Ray ray;
    public Vector3 intersection = new Vector3();
    Vector3 offset;
    public float distance;

    public SlopeRay(Vector3 offset) {
        this.offset = offset;
        this.ray = new Ray();
        this.distance = 0;
    }

    public void update(Vector3 pos) {
        ray.set(pos.cpy().add(offset), new Vector3(0, -1, 0));
        for (GameObject obj : targetObjects) {
            PavIntersector.intersect(this.ray, obj.bounds, obj.scene.modelInstance.transform, intersection);
        }
        distance = pos.cpy().add(offset).dst(intersection);
    }
}
