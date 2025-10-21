package com.pavengine.app.PavScript.Enemies;

public class EnemyBlueprint {
    String name;
    String[] animationNames;
    float yOffset, speed, health, damage;

    public EnemyBlueprint(String name, String[] animationNames, float yOffset, float speed, float health, float damage) {
        this.name = name;
        this.yOffset = yOffset;
        this.animationNames = animationNames;
        this.speed = speed;
        this.health = health;
        this.damage = damage;
    }
}
