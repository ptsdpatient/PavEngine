package com.pavengine.app.PavScript;

import static com.pavengine.app.Methods.files;

import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;

public class Bullet {
    public Vector3 pos;
    public Vector3 dir;
    public float speed = 10f;
    public Sprite sprite;

    public Bullet(Vector3 startPos, Vector3 direction, String name) {
        this.pos = new Vector3(startPos);
        this.dir = new Vector3(direction).nor();
        this.sprite = new Sprite(new Texture(files(name)));
    }

    public void update(float delta) {
        pos.mulAdd(dir, speed * delta); // move forward
    }

    public void draw(SpriteBatch batch, PerspectiveCamera camera) {
        Vector3 screenPos = camera.project(new Vector3(pos));
        sprite.setPosition(screenPos.x - sprite.getWidth() / 2, screenPos.y - sprite.getHeight() / 2);
        sprite.draw(batch);
    }
}
