package com.pavengine.app.PavGameObject;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.ObjectBehaviorType.AttachToCamera;
import static com.pavengine.app.ObjectBehaviorType.AttachToObject;
import static com.pavengine.app.ObjectBehaviorType.Static;
import static com.pavengine.app.PavCamera.PavCamera.camera;

import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Direction;
import com.pavengine.app.InteractType;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavBounds.PavBounds;

import net.mgsx.gltf.scene3d.scene.Scene;

import java.util.Objects;

public class StaticObject extends GameObject {

    public StaticObject() {

    }

    public StaticObject(String name, Vector3 position, float size, Scene scene, ObjectType objectType) {
        this.name = name;

        this.objectType = objectType;
        this.scene = scene;
        this.size = new Vector3(size, size, size);
        this.pos = position;
        this.scene.modelInstance.transform.setToTranslation(position);
        if (Objects.equals(this.name, "laser")) {
            objectBehaviorType = AttachToObject;
        } else {
            objectBehaviorType = Static;
        }
        this.scene.modelInstance.transform.scl(size);
        this.bounds = new BoundingBox();
        this.scene.modelInstance.calculateBoundingBox(bounds);
        bounds.min.add(pos);
        bounds.max.add(pos);
        this.radius = center.dst(bounds.max);

        this.scene.modelInstance.calculateBoundingBox(bounds);
        bounds.min.add(pos);
        bounds.max.add(pos);
        boxes.add(new PavBounds(bounds));

        pavBounds.setBounds(bounds);

        for(Animation animation : this.scene.modelInstance.animations) {
            animationNames.add(animation.id);
        }

        updateCenter();
        updateBox();

        update(0);

    }

    public StaticObject(String name, ObjectType objectType, Scene scene, Vector3 position, Quaternion rotation, Vector3 size, Array<PavBounds> boxes) {
        this.name = name;
        this.scene = scene;

        this.objectType =objectType;
        this.rotation = rotation;
        this.size = size;
        this.pos = position;
        this.scene.modelInstance.transform.setToTranslation(position);
        this.scene.modelInstance.transform.scl(size);

        this.bounds = new BoundingBox();
        this.scene.modelInstance.calculateBoundingBox(bounds);
        bounds.min.add(pos);
        bounds.max.add(pos);
        this.radius = center.dst(bounds.max);

        this.scene.modelInstance.calculateBoundingBox(bounds);
        bounds.min.add(pos);
        bounds.max.add(pos);
        this.boxes.add(new PavBounds(bounds));
        pavBounds.setBounds(bounds);

        this.boxes.addAll(boxes);

        for(Animation animation : this.scene.modelInstance.animations) {
            animationNames.add(animation.id);
        }

        updateCenter();
        updateBox();

        update(0);
    }

    public void setInteractAction(InteractType type) {
        this.interactible = true;
        this.interactType = type;
    }

    public boolean checkCollision(Vector3 position) {
        this.bounds.getCenter(center);
        center.mul(this.scene.modelInstance.transform);
        return position.dst(center) < radius;
    }

    public void setRing(float ringRadius, float ringHeightOffset) {
        this.ringDetection = true;
        this.ringRadius = ringRadius;
        this.ringHeightOffset = ringHeightOffset;
    }

    public float distanceFrom(Vector3 position) {
        this.bounds.getCenter(center);
        center.mul(this.scene.modelInstance.transform);
        return position.dst(center);
    }

    public boolean checkCollision(GameObject otherObject) {
        return pavBounds.getBounds().getCenter(new Vector3()).mul(scene.modelInstance.transform).dst(otherObject.pavBounds.getBounds().getCenter(new Vector3()).mul(otherObject.scene.modelInstance.transform)) < 5f;
    }


    public void rotate(Direction direction, float amplitude) {
        rotation.idt().setFromAxis(direction.dir, amplitude);
    }

    public void updateCenter() {
        this.pavBounds.getBounds().getCenter(center);
        center.mul(this.scene.modelInstance.transform);
    }

    public void playAnimation(int index, boolean loop, boolean force) {
        if ((animated && !force) || this.scene.animationController == null) return;

        currentAnimation = index;
        animated = true;

        scene.animationController.setAnimation(animationNames.get(index), loop ? -1 : 1, new AnimationController.AnimationListener() {

            @Override
            public void onEnd(AnimationController.AnimationDesc animation) {
                animated = false;
                animation.time = 0f;
            }

            @Override
            public void onLoop(AnimationController.AnimationDesc animation) {

            }
        });
    }

    public void moveTowards(Vector3 target, float speed, float delta) {
        pos.add(new Vector3(target.x - pos.x, 0, target.z - pos.z).nor().scl(speed * delta));
        dir = (new Vector3(target).sub(pos)).nor();
        rotation = new Quaternion().setEulerAngles(90 + MathUtils.atan2(dir.x, dir.z) * MathUtils.radiansToDegrees, 0, 0);
    }

    public void attachToObject(GameObject object, Vector3 offset) {
        attachObject = object;
        this.offset = offset;
        this.objectBehaviorType = AttachToObject;
        pos.set(attachObject.pos.cpy());
        pos.add(offset.cpy());
    }

    public void attachToCamera(Vector3 offset) {
        this.offset = offset;
        camera.view.getRotation(rotation);
        rotation.conjugate();
        objectBehaviorType = AttachToCamera;
        pos.set(camera.position);
        pos.add(offset.cpy().rot(new Matrix4().set(rotation)));
    }

    public void setScale(Vector3 size) {
        this.size = size;
        this.scene.modelInstance.transform.scl(size);
    }

    public void update(float delta) {
        scene.modelInstance.transform.set(pos, rotation, size);
    }


    @Override
    public void slopeDetection() {

    }

    public boolean contains(Vector3 point) {
        return pavBounds.contains(point);
    }

    public void updateBox() {
        scene.modelInstance.calculateBoundingBox(bounds);

        for(PavBounds bound : boxes) {
            bound.set(new Matrix4(pos, rotation, size));
        }

        pavBounds.set( bounds,
            new Matrix4(pos, rotation, size));
    }

    @Override
    public void playAnimation(String animationName, boolean loop, boolean force) {
        int i = 0;
        print("find : " + animationName);

        for(String string : animationNames) {
            print(string);
            if(Objects.equals(string, animationName)) {
                print("found animation");

                if ((animated && !force) || this.scene.animationController == null) return;

                currentAnimation = i;
                animated = true;

                scene.animationController.setAnimation(animationName, loop ? -1 : 1, new AnimationController.AnimationListener() {

                    @Override
                    public void onEnd(AnimationController.AnimationDesc animation) {
                        animated = false;
                        animation.time = 0f;
                    }

                    @Override
                    public void onLoop(AnimationController.AnimationDesc animation) {

                    }
                });

                break;
            }
            i++;
        }
    }

    @Override
    public float getHeight() {
        return bounds.getHeight() * size.y;
    }

    @Override

    public float getWidth() {
        return bounds.getWidth() * size.x;
    }

    @Override
    public float getDepth() {
        return bounds.getDepth() * size.z;
    }

}
