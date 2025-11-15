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
    public Vector3
        position = new Vector3(0,0,0),
        scale = new Vector3(1f,1f,1f);
    public Vector3 ringOffset = new Vector3();
    public Vector3[] rings = new Vector3[24];
    public Quaternion rotation = new Quaternion();
    public Vector3 center = new Vector3(0,0,0);

    private static final BoundingBox CANONICAL_BOX =
        new BoundingBox(new Vector3(-0.5f, -0.5f, -0.5f),
            new Vector3( 0.5f,  0.5f,  0.5f));

    public Matrix4 transform = new Matrix4();

    public float ringRadius=2f , heightOffset = 0f;

    public PavBounds(OrientedBoundingBox box) {
        this.box = box;
    }

    public PavBounds(BoundingBox bounds, Matrix4 transform) {
        box = new OrientedBoundingBox(bounds, transform);
    }

    public PavBounds(BoundingBox bounds, boolean isGround) {
        type = !isGround? PavBoundsType.Bound :PavBoundsType.Ground;
    }

    public PavBounds(PavBoundsType type) {
        box = new OrientedBoundingBox(CANONICAL_BOX);
        this.type = type;
    }

    public PavBounds(Vector3 min, Vector3 max, boolean isGround) {
        type = !isGround? PavBoundsType.Bound :PavBoundsType.Ground;
    }

    public PavBounds(BoundingBox bounds) {
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
        box.getBounds().getCenter(center);
        return center.mul(box.transform);
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

    public void setPosition(Vector3 pos) {
        this.position.set(pos);
        rebuild();
    }


    public void setSize(Vector3 newSize) {

        scale.set(
            Math.max(0.0001f, newSize.x),
            Math.max(0.0001f, newSize.y),
            Math.max(0.0001f, newSize.z)
        );

        rebuild();
    }

    private void rebuild() {

        transform.idt()
            .translate(position)
            .rotate(rotation)
            .scale(scale.x, scale.y, scale.z);

        box.set(CANONICAL_BOX, transform);
    }





    public boolean intersects(PavBounds orientedBoundingBox) {
        return orientedBoundingBox.box.intersects(box);
    }




//    public Vector3 getPosition() {
//        box.getTransform().getTranslation(center);
//        return center;
//    }
}
