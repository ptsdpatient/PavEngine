package com.pavengine.app.PavGameObject;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.ObjectBehaviorType.AttachToCamera;
import static com.pavengine.app.ObjectBehaviorType.AttachToObject;
import static com.pavengine.app.ObjectBehaviorType.Static;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.cameraBehavior;
import static com.pavengine.app.PavScreen.GameWorld.dynamicObjects;
import static com.pavengine.app.PavScreen.GameWorld.shapeRenderer;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.pavengine.app.Actions;
import com.pavengine.app.CameraBehaviorType;
import com.pavengine.app.Direction;
import com.pavengine.app.Force;
import com.pavengine.app.InputBehavior.InputBehavior;
import com.pavengine.app.InteractType;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.SlopeRay;

import net.mgsx.gltf.scene3d.scene.Scene;

import java.util.Objects;

public class KinematicObject extends GameObject {


    public KinematicObject() {

    }

    public KinematicObject(String name, Vector3 position, float size, Scene scene, float mass, float bounciness, ObjectType objectType, String[] animationNames) {
        this.name = name;
        this.animationNames = animationNames;
        this.objectType = objectType;
        this.scene = scene;
        this.size = new Vector3(size, size, size);
        this.mass = mass;
        this.pos = position;
        this.bounciness = bounciness;
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
        boxes.get(0).setBounds(bounds);

    }

    public KinematicObject(String name, Vector3 position, Scene scene, float mass, float bounciness, ObjectType objectType, String[] animationNames) {
        this.name = name;
        this.animationNames = animationNames;
        this.objectType = objectType;
        this.scene = scene;
        this.mass = mass;
        this.pos = position;
        this.bounciness = bounciness;
        this.scene.modelInstance.transform.setToTranslation(position);
        this.bounds = new BoundingBox();
        this.scene.modelInstance.calculateBoundingBox(bounds);
        bounds.min.add(pos);
        bounds.max.add(pos);
        objectBehaviorType = Static;
        this.radius = center.dst(bounds.max);

        this.scene.modelInstance.calculateBoundingBox(bounds);
        bounds.min.add(pos);
        bounds.max.add(pos);
        boxes.add(new PavBounds(bounds));
        boxes.get(0).setBounds(bounds);

    }

    public KinematicObject(String name, Scene scene, Vector3 position, Quaternion rotation, Vector3 size) {
        this.name = name;
        this.scene = scene;

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
        boxes.add(new PavBounds(bounds));
        boxes.get(0).setBounds(bounds);

        update(0);
    }



    public Quaternion getCameraRotation() {
        Vector3 dir = new Vector3(camera.direction).set(camera.direction.x, 0, camera.direction.z).nor();

        float yawRad = (float) Math.atan2(dir.x, dir.z);

        return new Quaternion().setFromAxisRad(Vector3.Y, yawRad);
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

    public float distanceFrom(Vector3 position) {
        this.bounds.getCenter(center);
        center.mul(this.scene.modelInstance.transform);
        return position.dst(center);
    }

    public boolean checkCollision(GameObject otherObject) {
        return boxes.get(0).getBounds().getCenter(new Vector3()).mul(scene.modelInstance.transform).dst(otherObject.boxes.get(0).getBounds().getCenter(new Vector3()).mul(otherObject.scene.modelInstance.transform)) < 5f;
    }


    public void rotate(Direction direction, float amplitude) {
        rotation.idt().setFromAxis(direction.dir, amplitude);
    }

    public void updateCenter() {
        this.boxes.get(0).getBounds().getCenter(center);
        center.mul(this.scene.modelInstance.transform);
    }

    public boolean checkVerticalCollision(float distance) {
        if (pos.y <= 0) {
            pos.y = 0;
            return true;
        }

        for (int i = 0; i < dynamicObjects.size; i++) {
            if (dynamicObjects.get(i) == this || dynamicObjects.get(i).hasBounced || dynamicObjects.get(i).grounded)
                continue;
            Vector3 otherCenter = dynamicObjects.get(i).center;
            if (otherCenter.dst(center) < distance) {
                if (center.y > otherCenter.y) {
                    if (forces.size < 1)
                        forces.add(new Force(new Vector3(MathUtils.cos(MathUtils.random(0f, MathUtils.PI2)), 0, MathUtils.sin(MathUtils.random(0f, MathUtils.PI2))).nor(), 2));

//                    float totalMass = this.mass+dynamicObjects.get(i).mass;
//                    dynamicObjects.get(i).vy=(vy * (this.mass - dynamicObjects.get(i).mass) + 2 * dynamicObjects.get(i).mass * dynamicObjects.get(i).vy) / totalMass;
//                    this.vy = (dynamicObjects.get(i).vy * (dynamicObjects.get(i).mass - this.mass) + 2 * this.mass * this.vy) / totalMass;
//                    dynamicObjects.get(i).hasBounced=true;
                    playAnimation(0, false, false);
                    return true;
                }
            }
        }
        return false;
    }

    public void playAnimation(int index, boolean loop, boolean force) {
        if ((animated && !force) || this.scene.animationController == null) return;

        currentAnimation = index;
        animated = true;

        scene.animationController.setAnimation(animationNames[index], loop ? -1 : 1, new AnimationController.AnimationListener() {

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
        this.objectBehaviorType = AttachToCamera;
        pos.set(camera.position);
        pos.add(offset.cpy().rot(new Matrix4().set(rotation)));
    }

    public void setScale(Vector3 size) {
        this.size = size;
        this.scene.modelInstance.transform.scl(size);
    }

    public void reboundForce() {
//        for (GameObject obj : targetObjects) {
//            if (
//                boxes.get(0).intersects(obj.box)
//            ) {
////                print("rebound");
//                forces.clear();
////                forces.add(new Force(pos.cpy().sub(obj.pos).nor(), 2));
//            }
//        }
    }

    public void update(float delta) {
        if (this.scene.animationController != null) scene.animationController.update(delta);
        testRotation += 1;
//        if(Objects.equals(name, "pencil"))print("" + this.rotation);
//        if(Objects.equals(name, "pencil"))print("" + attachObject.rotation);
//
//        if (gravity) {
//
//        }

        updateCenter();
        updateBottom();
        updateInputBehavior(delta);
        updateBox();

        for (Actions action : actions) {
            action.update(delta);
            switch (action.action) {
                case Rotate: {
                    rotation.mul(new Quaternion().set(action.direction.dir, action.amplitude * (action.eased - action.lastEased)));
                    action.lastEased = action.eased;
                }
                break;
                case OscilateMotion: {

                }
                break;
            }
            if (action.elapsed >= action.duration) {
                actions.removeValue(action, true);
            }
        }


//        reboundForce();


        for (Force f : forces) {
            pos.add(f.update(delta, true));
            if (f.amplitude <= 0) {
                forces.removeValue(f, true);
            }
        }


        switch (objectBehaviorType) {
            case FollowPlayer: {
                moveTowards(camera.position, 3f, delta);
                rotate(Direction.LEFT, 10);
            }
            break;

            case AttachToObject: {
                pos.set(attachObject.pos.cpy()).add(offset.cpy().rot(new Matrix4().set(attachObject.rotation.cpy())));

                Vector3 dir = forwardDirection.cpy().nor();
                rotation.set(attachObject.rotation);

                if (cameraBehavior == CameraBehaviorType.TopDown) {
                    // Only yaw
                    float yaw = (float) Math.toDegrees(Math.atan2(dir.x, dir.z)) + 90f; // Blender offset
                    rotation.mul(new Quaternion().setEulerAngles(yaw, 0f, 0f));
                } else {
                    // Yaw + pitch (ignore roll)
                    float yaw = (float) Math.toDegrees(Math.atan2(dir.x, dir.z)) + 90f; // Blender offset
                    float pitch = (float) Math.toDegrees(Math.asin(-dir.y));             // vertical tilt
                    rotation.mul(new Quaternion().setEulerAngles(yaw, pitch, 0f));      // roll = 0
                }
            }
            break;

            case AttachToCamera: {
                pos.set(camera.position.cpy().add(offset));
                rotation.idt()
                    .setFromAxis(Vector3.Y, (float) Math.atan2(-camera.direction.x, -camera.direction.z) * MathUtils.radiansToDegrees)
                    .mul(new Quaternion().setFromAxis(Vector3.X, (float) Math.asin(camera.direction.y) * MathUtils.radiansToDegrees));
            }
            break;
        }
//        print("" + camera.position);
        scene.modelInstance.transform.set(pos, rotation, size);


    }

    private void updateInputBehavior(float delta) {
        for (InputBehavior behavior : inputBehaviorList) {
            behavior.update(this, delta);
        }
    }

    public void setRing(float ringRadius, float ringHeightOffset) {
        this.ringDetection = true;
        this.ringRadius = ringRadius;
        this.ringHeightOffset = ringHeightOffset;

        boxes.get(0).heightOffset = ringHeightOffset;
        boxes.get(0).ringRadius = ringRadius;

        footBox.heightOffset = ringHeightOffset-2;
        footBox.ringRadius = ringRadius;
    }

    private void updateBottom() {
        this.boxes.get(0).getBounds().getCenter(bottom);
        bottom.mul(this.scene.modelInstance.transform);
        bottom.sub(new Vector3(0, getHeight(), 0));
    }

    @Override
    public void slopeDetection() {

        for (SlopeRay ray : slopeRays) {
            ray.update(pos);
        }

        slopeNormal = slopeRays.get(1).intersection.cpy()
            .sub(slopeRays.get(0).intersection)
            .crs(slopeRays.get(2).intersection.cpy().sub(slopeRays.get(0).intersection))
            .nor();

    }

    public boolean contains(Vector3 point) {
        return boxes.get(0).contains(point);
    }

    public void updateBox() {
        scene.modelInstance.calculateBoundingBox(bounds);

        boxes.get(0).set(
            new BoundingBox(
                new Vector3(
                    bounds.min.x,
                    bounds.min.y,
                    bounds.min.z
                ),
                new Vector3(
                    bounds.max.x,
                    bounds.max.y,
                    bounds.max.z
                )
            ),
            new Matrix4(pos.cpy(), rotation.cpy(), size.cpy())
        );

        if (ringDetection) {
            boxes.get(0).updateRings(pos.cpy(),rotation.cpy());
            footBox.updateRings(pos.cpy(),rotation.cpy());
        }

        if (detectSlope) footBox.set(
            new BoundingBox(
                new Vector3(
                    bounds.min.x,
                    bounds.min.y + 0.25f,
                    bounds.min.z
                ),
                new Vector3(
                    bounds.max.x,
                    bounds.max.y - 1.5f,
                    bounds.max.z
                )
            ),
            new Matrix4(center.cpy().sub(0, 1, 0), rotation.cpy(), size.cpy())
        );
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
