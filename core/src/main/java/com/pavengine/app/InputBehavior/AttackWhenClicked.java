package com.pavengine.app.InputBehavior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavGameObject.GameObject;

public class AttackWhenClicked implements InputBehavior {
    float delay, duration;
    float fireRate;
    float fireTime;
    private int button;

    public AttackWhenClicked(int button, float delay, float duration) {
        this.button = button;
        this.delay = delay;
        this.duration = duration;
    }

    @Override
    public void update(GameObject obj, float delta) {

        if (Gdx.input.isButtonJustPressed(button) && fireTime <= 0) {
            fireTime = fireRate;
        }

        if (fireTime >= 0) {
            fireTime -= delta;
            obj.attackBox = fireRate - fireTime > delay && fireRate - fireTime < (delay + duration) ? new PavBounds(
                new BoundingBox(
                    new Vector3(-2f, -2f, -2f),
                    new Vector3(2f, 2f, 2f)
                ),
                new Matrix4(obj.pos.cpy().add(obj.attackOffset.cpy().rot(new Matrix4().set(obj.rotation))), obj.rotation, new Vector3(1, 1, 1))) : new PavBounds();
//            print("");
        }
    }
}
