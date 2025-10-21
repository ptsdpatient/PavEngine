package com.pavengine.app;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;

public class PavIntersector {
    private static final Vector3 tmpCenter = new Vector3();
    private static final Vector3 tmpExtents = new Vector3();
    private static final Vector3 tmpScale = new Vector3();
    private static final Quaternion tmpQuat = new Quaternion();

    private static final Vector3[] axes = {
        new Vector3(), new Vector3(), new Vector3()
    };

    /**
     * Ray vs Oriented Bounding Box intersection.
     *
     * @param ray             Ray in world space
     * @param aabb            Local-space bounding box of the model (unscaled, from Mesh)
     * @param transform       ModelInstance transform (translation, rotation, scale)
     * @param outIntersection World-space intersection point (if hit)
     * @return true if ray intersects OBB
     */

    public static boolean intersect(Ray ray, BoundingBox aabb, Matrix4 transform, Vector3 outIntersection) {
        aabb.getCenter(tmpCenter);
        aabb.getDimensions(tmpExtents).scl(0.5f);

        transform.getScale(tmpScale);
        tmpExtents.scl(tmpScale);

        tmpCenter.mul(transform);

        transform.getRotation(tmpQuat);
        axes[0].set(1, 0, 0).rot(new Matrix4().set(tmpQuat)).nor();
        axes[1].set(0, 1, 0).rot(new Matrix4().set(tmpQuat)).nor();
        axes[2].set(0, 0, 1).rot(new Matrix4().set(tmpQuat)).nor();

        return intersectRayOBB(ray, outIntersection);
    }

    private static boolean intersectRayOBB(Ray ray,
                                           Vector3 outIntersection) {
        float tMin = -Float.MAX_VALUE;
        float tMax = Float.MAX_VALUE;

        Vector3 p = new Vector3(PavIntersector.tmpCenter).sub(ray.origin);
        float[] he = {PavIntersector.tmpExtents.x, PavIntersector.tmpExtents.y, PavIntersector.tmpExtents.z};

        for (int i = 0; i < 3; i++) {
            Vector3 axis = PavIntersector.axes[i];

            float e = axis.dot(p);
            float f = axis.dot(ray.direction);

            if (Math.abs(f) > 1e-6f) {
                float t1 = (e - he[i]) / f;
                float t2 = (e + he[i]) / f;

                if (t1 > t2) {
                    float tmp = t1;
                    t1 = t2;
                    t2 = tmp;
                }

                if (t1 > tMin) tMin = t1;
                if (t2 < tMax) tMax = t2;

                if (tMin > tMax) return false;
                if (tMax < 0f) return false;
            } else {
                if (e < -he[i] || e > he[i]) return false;
            }
        }

        float tHit = (tMin > 0f) ? tMin : tMax;

        outIntersection.set(ray.origin).mulAdd(ray.direction, tHit);
        return true;
    }
}
