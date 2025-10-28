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
    Vector2 position;
    public static Rectangle clickArea;

    public PavCursor(String name, float sensitivity) {
        cursors = extractSprites(name, 32, 32);
        position = new Vector2(resolution.x / 2f, resolution.y / 2f);
        cursor = new Sprite(cursors[1]);
        this.sensitivity = sensitivity;
        clickArea = new Rectangle(0,0,10,10).setPosition(position);
        clickArea.setPosition(position);
    }

    public void setCursor(int index) {
        print("setting cursor");
        this.index = index;
        cursor.setRegion(cursors[index]);
        cursor.setPosition(position.x, position.y);
    }

    public void move(Vector2 translate) {

    }

    public void draw(SpriteBatch sb, float delta) {
        cursor.draw(sb);
        position.add(
            Gdx.input.getDeltaX() * delta * sensitivity,
            -Gdx.input.getDeltaY() * delta * sensitivity
        );
        cursor.setPosition(position.x, position.y);
        clickArea.setPosition(
            position.cpy().add(0,cursor.getHeight() - 10f)
        );
    }

    public boolean clicked(Rectangle box) {
        return clickArea.overlaps(box);
    }

}
