package com.pavengine.app.PavBounds;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;

public class PavBounds {
    public OrientedBoundingBox box = new OrientedBoundingBox();
    public boolean isGround = false;
    public Vector3 offset = new Vector3(), min = new Vector3(), max = new Vector3();
    public Vector3[] rings = new Vector3[24];

    public PavBounds(OrientedBoundingBox box) {
        this.box = box;
    }

    public PavBounds(BoundingBox bounds, Matrix4 transform) {
        box = new OrientedBoundingBox(bounds, transform);
    }

    public PavBounds(BoundingBox bounds, boolean isGround) {
        min = bounds.min;
        max = bounds.max;
        this.isGround = isGround;
    }

    public PavBounds(Vector3 min, Vector3 max, boolean isGround) {
        this.min = min;
        this.max = max;
        this.isGround = isGround;
    }

    public PavBounds(BoundingBox bounds) {
        min = bounds.min;
        max = bounds.max;
        box = new OrientedBoundingBox(bounds);
    }

    public PavBounds() {

    }

    public boolean ringOverlaps(PavBounds obj, Vector3 nextPos, float ringRadius, float heightOffset) {
        for (int i = 0; i < rings.length; i++) {
            if (obj.contains(
                new Vector3(
                    nextPos.x + ringRadius * MathUtils.cos((MathUtils.PI2 * i) / rings.length),
                    nextPos.y + heightOffset,
                    nextPos.z + ringRadius * MathUtils.sin((MathUtils.PI2 * i) / rings.length)
                )
            )
            ) return true;
        }
        return false;
    }

    public boolean containsRing(Vector3[] rings, Vector3 nextPos, float ringRadius, float heightOffset) {
//        print("checking");
        for (int i = 0; i < rings.length; i++) {
            if (!box.contains(
                new Vector3(
                    nextPos.x + ringRadius * MathUtils.cos((MathUtils.PI2 * i) / rings.length),
                    nextPos.y + heightOffset,
                    nextPos.z + ringRadius * MathUtils.sin((MathUtils.PI2 * i) / rings.length)
                )
            )) return false;
        }
        return true;
    }

    public void updateRings(Vector3 pos, float ringRadius, float heightOffset) {
        for (int i = 0; i < rings.length; i++) {
            rings[i] =
                new Vector3(
                    pos.x + ringRadius * MathUtils.cos((MathUtils.PI2 * i) / rings.length),
                    pos.y + heightOffset,
                    pos.z + ringRadius * MathUtils.sin((MathUtils.PI2 * i) / rings.length)
                );
        }

    }


    public Vector3 getCenter() {
        Vector3 center = new Vector3();
        box.getBounds().getCenter(center);       // local center
        return center.mul(box.transform);        // transform into world space
    }

    public boolean intersect(OrientedBoundingBox oob) {
        return oob.intersects(box);
    }

    public Vector3[] getVertices() {
        return box.getVertices();
    }

    public BoundingBox getBounds() {
        return box.getBounds();
    }

    public void setBounds(BoundingBox bounds) {
        box.setBounds(bounds);
    }

    public boolean contains(OrientedBoundingBox oob) {
        return oob.contains(box);
    }


    public boolean contains(Vector3 point) {
        return box.contains(point);
    }

    public void set(BoundingBox bounds, Matrix4 transform) {
        box.set(bounds, transform);
    }

    public boolean intersects(PavBounds orientedBoundingBox) {
        return orientedBoundingBox.box.intersects(box);
    }
}
