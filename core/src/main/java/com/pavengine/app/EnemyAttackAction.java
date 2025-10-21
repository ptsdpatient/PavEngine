package com.pavengine.app;

public class EnemyAttackAction {
    public int animationIndex;
    public float start;
    public float duration;

    public EnemyAttackAction(int animationIndex, float start, float duration) {
        this.animationIndex = animationIndex;
        this.start = start;
        this.duration = duration;
    }
}
