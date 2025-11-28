package com.pavengine.app;

import static com.pavengine.app.Methods.extractSprites;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.resolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PavCursor {

    public int index = 0;
    public Sprite cursor;
    TextureRegion[] cursors;
    float sensitivity;
    Vector2 position;
    public Rectangle clickArea;
    private final Vector2 tmp = new Vector2();

    public PavCursor(String name, float sensitivity) {
        cursors = extractSprites(name, 32, 32);
        position = new Vector2(resolution.x / 2f, resolution.y / 2f);
        cursor = new Sprite(cursors[1]);
        this.sensitivity = sensitivity;
        clickArea = new Rectangle(0,0,10,10).setPosition(position);
        clickArea.setPosition(position);
    }

    public void setCursor(int index) {
        this.index = index;
        cursor.setRegion(cursors[index]);
        cursor.setPosition(position.x, position.y);
    }

    public Vector2 getPosition() {
        return new Vector2(clickArea.x,clickArea.y);
    }

    public void draw(SpriteBatch sb, float delta) {
        cursor.draw(sb);

        position.add(
            (Math.abs(Gdx.input.getDeltaX()) < 80? Gdx.input.getDeltaX() : 0f) * delta * sensitivity,
            (Math.abs(Gdx.input.getDeltaY()) < 80? -Gdx.input.getDeltaY() : 0f)  * delta * sensitivity
        );

        position.set(MathUtils.clamp(position.x,-32,resolution.x),MathUtils.clamp(position.y,-32,resolution.y));


        cursor.setPosition(position.x, position.y);

        tmp.set(position.x, position.y + cursor.getHeight() - 10f);

        clickArea.setPosition(tmp);

    }

    public boolean clicked(Rectangle box) {
        return clickArea.overlaps(box);
    }

}
