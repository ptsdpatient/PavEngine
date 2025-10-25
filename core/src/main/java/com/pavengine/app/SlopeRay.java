package com.pavengine.app;

import static com.pavengine.app.PavScreen.GameWorld.groundObjects;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
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
        this.offset = new Vector3(offset.x,offset.y+1,offset.z);
        this.ray = new Ray();
        this.distance = 0;
    }

    public void update(Vector3 pos) {
        // Set downward ray, normalized
        ray.set(pos.cpy().add(offset), new Vector3(0, -1, 0).nor());

        float closestDist = Float.MAX_VALUE;
        boolean hit = false;

        for (GameObject obj : groundObjects) {
            if (PavIntersector.intersect(ray, obj.bounds, obj.scene.modelInstance.transform, intersection)) {
                float d = ray.origin.dst(intersection);
                if (d < closestDist) {
                    closestDist = d;
                    distance = d;
                    hit = true;
                }
            }
        }

        if (!hit) {
            // No intersection found, show a downward line of 1 unit
//            intersection.set(ray.origin).add(0, -1, 0);
//            distance = 1f;
        }
    }


}
