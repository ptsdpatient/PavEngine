package com.pavengine.app;

public class EnemyBehavior {
    float timeAlive;
    ObjectBehaviorType type;

    public EnemyBehavior(ObjectBehaviorType type, float timeAlive) {
        this.timeAlive = timeAlive;
        this.type = type;
    }

    public void update(float delta) {
        timeAlive -= delta;

    }
}
