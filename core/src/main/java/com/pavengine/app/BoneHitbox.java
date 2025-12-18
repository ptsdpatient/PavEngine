package com.pavengine.app;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;

public class BoneHitbox {
    public String boneName;

    public BoundingBox localBounds;     // in bone space
    public Matrix4 worldTransform = new Matrix4(); // animated

    public OrientedBoundingBox obb;

    public BoneHitbox(String boneName, Vector3 localCenter, Vector3 localHalfExtents) {
        this.boneName = boneName;

        // Build local bounding box from center + half extents
        Vector3 min = new Vector3(localCenter).sub(localHalfExtents);
        Vector3 max = new Vector3(localCenter).add(localHalfExtents);

        this.localBounds = new BoundingBox(min, max);

        this.obb = new OrientedBoundingBox(); // empty for now, we set it each frame
    }
}


