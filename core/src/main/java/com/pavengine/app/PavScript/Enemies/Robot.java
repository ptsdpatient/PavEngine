package com.pavengine.app.PavScript.Enemies;

import static com.pavengine.app.PavEngine.gamePause;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavScreen.GameScreen.bloodEffect;
import static com.pavengine.app.PavScreen.GameScreen.damageSpark;
import static com.pavengine.app.PavScreen.GameScreen.explodeEffect;
import static com.pavengine.app.PavScreen.GameScreen.robots;
import static com.pavengine.app.PavScreen.GameScreen.world;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavScript.Lane;

import java.util.Objects;

public class Robot extends Enemy {
    Lane lane;
    float speed, yOffset;
    boolean sparking = false;
    boolean explode = false;
    float damage;

    public Robot(Lane lane, EnemyBlueprint bp) {
        this.yOffset = bp.yOffset;
        this.lane = new Lane(lane.start.cpy(), lane.end.cpy());
//        world.addObject("robot", bp.name, lane.start.cpy().add(new Vector3(0, yOffset, 0)), 0, 0, ObjectType.TARGET, bp.animationNames);
        object = targetObjects.get(targetObjects.size - 1);
        if (Objects.equals(bp.name, "prime")) object.size = new Vector3(3, 3, 3);
        int i = 0;
        this.health = bp.health;
        this.speed = bp.speed;
        this.damage = bp.damage;
        for (String an : bp.animationNames) {
            object.playAnimation(i, true, true);
            i++;
        }
    }

    public void update(float delta) {
        if (health <= 0) {
            explodeIfNeeded();
            removeSelf();
            return;
        }

        // Calculate direction and distance
        Vector3 direction = lane.end.cpy().sub(object.pos);
        float horizontalDistance = Vector2.len(direction.x, direction.z); // ignore height
        float totalDistance = direction.len();

        // Health effects
        if (health < 30 && !sparking) {
            sparking = true;
            damageSpark.spawn(object.center);
        }

        if (health < 10 && !explode) {
            explode = true;
            explodeEffect.spawn(object.center);
        }

        // Move towards target
        if (totalDistance > 0.001f) {
            direction.nor().scl(speed * delta);
            if (direction.len() > totalDistance)
                object.pos.set(lane.end);
            else
                object.pos.add(direction);

            object.pos.y = lane.start.y + yOffset;
        }

        // Check horizontal proximity for despawn/damage
        if (horizontalDistance < 0.4f) {
            PavEngine.health -= damage;
            if (!gamePause) {
                bloodEffect.spawn(overlayCamera.position.x, overlayCamera.position.y, 7);
            }
            removeSelf();
        }
    }


    private void explodeIfNeeded() {
        if (!explode) {
            explode = true;
            explodeEffect.spawn(object.center);
        }
    }

    private void removeSelf() {
        soundBox.playSound(MathUtils.randomBoolean() ? "robot_damage_1.mp3" : "robot_damage_2.mp3");
        PavEngine.credits += MathUtils.random(0, 5);
        robots.removeValue(this, true);
        sceneManager.removeScene(object.scene);
        targetObjects.removeValue(object, true);
    }


    @Override
    public GameObject getObject() {
        return this.object;
    }
}
