package com.pavengine.app.PavBounds;

import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;


public class PavBounds {
    public OrientedBoundingBox box = new OrientedBoundingBox();
    public PavBoundsType type = PavBoundsType.Bound;
    public Vector3 offset = new Vector3(), min = new Vector3(), max = new Vector3();
    public Vector3[] rings = new Vector3[24];

    public float ringRadius=2f ,heightOffset =0f;

    public PavBounds(OrientedBoundingBox box) {
        this.box = box;
    }

    public PavBounds(BoundingBox bounds, Matrix4 transform) {
        box = new OrientedBoundingBox(bounds, transform);
    }

    public PavBounds(BoundingBox bounds, boolean isGround) {
        min = bounds.min;
        max = bounds.max;
        type = !isGround? PavBoundsType.Bound :PavBoundsType.Ground;
    }

    public PavBounds(PavBoundsType type) {
        min = new Vector3(-1f,-1f,-1f);
        max = new Vector3(1f,1f,1f);
        box = new OrientedBoundingBox(new BoundingBox(min,max));
        this.type = type;
    }

    public PavBounds(Vector3 min, Vector3 max, boolean isGround) {
        this.min = min;
        this.max = max;
        type = !isGround? PavBoundsType.Bound :PavBoundsType.Ground;
    }

    public PavBounds(BoundingBox bounds) {
        min = bounds.min;
        max = bounds.max;
        box = new OrientedBoundingBox(bounds);
    }

    public PavBounds() {

    }

    public boolean ringOverlaps(PavBounds obj, Vector3 nextPos) {
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

    public boolean containsRing(Vector3[] rings, Vector3 nextPos) {

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

//    public void updateRings(Vector3 pos) {
//        for (int i = 0; i < rings.length; i++) {
//
//            rings[i] =
//                new Vector3(
//                    pos.x + ringRadius * MathUtils.cos((MathUtils.PI2 * i) / rings.length),
//                    pos.y + heightOffset,
//                    pos.z + ringRadius * MathUtils.sin((MathUtils.PI2 * i) / rings.length)
//                );
//        }
//    }

    public void updateRings(Vector3 pos, Quaternion rotation) {
        for (int i = 0; i < rings.length; i++) {
            Vector3 offset = new Vector3(
                ringRadius * MathUtils.cos((MathUtils.PI2 * i) / rings.length),
                heightOffset,
                ringRadius * MathUtils.sin((MathUtils.PI2 * i) / rings.length)
            );
            offset.mul(rotation);
            rings[i] = pos.cpy().add(offset);
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
