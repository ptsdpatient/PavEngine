package com.pavengine.app.PavScript.Enemies;

import com.pavengine.app.PavGameObject.GameObject;

public abstract class Enemy {
    public float health;
    GameObject object;

    public abstract void update(float delta);

    public abstract GameObject getObject();
}
