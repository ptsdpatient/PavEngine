package com.pavengine.app.PavGameObject;

import static com.pavengine.app.ObjectBehaviorType.AttachToCamera;
import static com.pavengine.app.ObjectBehaviorType.AttachToObject;
import static com.pavengine.app.ObjectBehaviorType.Static;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavPlayer.PavPlayer.player;
import static com.pavengine.app.PavScreen.GameWorld.dynamicObjects;

import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Actions;
import com.pavengine.app.Direction;
import com.pavengine.app.Force;
import com.pavengine.app.InteractType;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavBounds.PavBounds;

import net.mgsx.gltf.scene3d.scene.Scene;

import java.util.Objects;

public class TargetObject extends GameObject {

    public TargetObject() {

    }

    public TargetObject(String name, Vector3 position, float size, Scene scene, float mass, float bounciness, ObjectType objectType, String[] animationNames) {
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
        pavBounds.setBounds(bounds);


    }

    public TargetObject(String name, Vector3 position, Scene scene, float mass, float bounciness, ObjectType objectType, String[] animationNames) {
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
        pavBounds.setBounds(bounds);


    }

    public TargetObject(String name, Scene scene, Vector3 position, Quaternion rotation, Vector3 size, Array<PavBounds> boxes) {
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
        this.boxes.add(new PavBounds(bounds));
        pavBounds.setBounds(bounds);

        this.boxes.addAll(boxes);

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
        objectBehaviorType = AttachToCamera;
        pos.set(camera.position);
        pos.add(offset.cpy().rot(new Matrix4().set(rotation)));
    }

    public void setRing(float ringRadius, float ringHeightOffset) {
        this.ringDetection = true;
        this.ringRadius = ringRadius;
        this.ringHeightOffset = ringHeightOffset;
    }

    public void setScale(Vector3 size) {
        this.size = size;
        this.scene.modelInstance.transform.scl(size);
    }

    public void update(float delta) {
        timeAlive += delta;
//        print(pos);
//        if(fireTime >= 0) {
//            fireTime -= delta;
//            attackBox = new OrientedBoundingBox(new BoundingBox(), new Matrix4(pos,rotation,new Vector3(1,1,1)));
//            world.debugOOB(attackBox);
//        }
//        debugCube(attackBox);

        if (isEnemy && fireTime > 0) {
            fireTime -= delta;

            attackBox = fireRate - fireTime > currentAttackAction.start && fireRate - fireTime < (currentAttackAction.start + currentAttackAction.duration) ? new PavBounds(
                new BoundingBox(
                    new Vector3(-2f, -2f, -2f),
                    new Vector3(2f, 2f, 2f)
                ),
                new Matrix4(pos.cpy().add(attackOffset.cpy().rot(new Matrix4().set(rotation))), rotation, new Vector3(1, 1, 1))) : new PavBounds();

        }

        updateCenter();
//        testRotation += delta;
        if (this.scene.animationController != null) scene.animationController.update(delta);

        updateBox();

        for (Actions action : actions) {
            action.update(delta);
            switch (action.action) {
                case Rotate: {
                    rotation.mul(new Quaternion().set(action.direction.dir, action.amplitude * (action.eased - action.lastEased)));
                    action.lastEased = action.eased;
                }
                break;
            }
            if (action.elapsed >= action.duration) {
                actions.removeValue(action, true);
            }
        }


        for (Force f : forces) {
            pos.add(f.update(delta, true));
            if (f.amplitude <= 0) {
                forces.removeValue(f, true);
            }
        }

        if (isEnemy) {
            float dist = player.pos.dst(pos);

            if (dist < attackRange) {
                if (fireTime <= 0) {
                    behave(delta);
                    fireTime = fireRate;
                    currentAttackAction = enemyAttackActionList.get(MathUtils.random(attackAnimation.length - 1));
                    playAnimation(currentAttackAction.animationIndex, false, true);
                }
            } else if (dist < behaveRange) {
                if (currentAnimation != 0) playAnimation(0, true, false);
                behave(delta);
            } else {
                if (currentAnimation != 0) playAnimation(0, true, false);
            }
        } else {
            behave(delta);
        }


        scene.modelInstance.transform.set(pos, rotation, size);

    }


    private void behave(float delta) {
        switch (objectBehaviorType) {
            case FollowPlayer: {
                moveTowards(camera.position, 3f, delta);
            }
            break;

            case LookAtPlayer: {
                dir = (new Vector3(player.pos).sub(pos)).nor();
                rotation.slerp(new Quaternion().setEulerAngles(forwardDirection.x + MathUtils.atan2(dir.x, dir.z) * MathUtils.radiansToDegrees, forwardDirection.y + 0, forwardDirection.z + 0), 0.25f);
//                rotation = ;
            }
            break;

            case AttachToObject: {
//                print(offset);
            }
            break;

            case AttachToCamera: {
            }
            break;
        }
    }

    @Override
    public void slopeDetection() {

    }

    public boolean contains(Vector3 point) {
        return pavBounds.contains(point);
    }

    public void updateBox() {
        scene.modelInstance.calculateBoundingBox(bounds);
        pavBounds.set(
            new BoundingBox(
                bounds.min.add(padding),
                bounds.max.add(padding)
            )
            , new Matrix4().set(pos, rotation, size));
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
