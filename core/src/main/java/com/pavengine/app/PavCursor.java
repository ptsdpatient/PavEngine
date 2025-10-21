package com.pavengine.app;

import static com.pavengine.app.Methods.extractSprites;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.resolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PavCursor {
    public int index = 0;
    public Sprite cursor;
    TextureRegion[] cursors;
    float sensitivity;

    public PavCursor(String name, float sensitivity) {
        cursors = extractSprites(name, 32, 32);
        cursor = new Sprite(cursors[1]);
        this.sensitivity = sensitivity;
    }

    public void setCursor(int index) {
        this.index = index;
        cursor.setRegion(cursors[index]);
        cursor.setPosition(resolution.x / 2f, resolution.y / 2f);
    }

    public void move(Vector2 translate) {

    }

    public void draw(SpriteBatch sb, float delta) {
        cursor.draw(sb);
        cursor.translate(Gdx.input.getDeltaX() * delta * sensitivity, -Gdx.input.getDeltaY() * delta * sensitivity);
    }



    public boolean clicked(Rectangle box) {
        return cursor.getBoundingRectangle().overlaps(box);
    }
}
