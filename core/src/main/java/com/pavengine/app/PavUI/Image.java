package com.pavengine.app.PavUI;

import static com.pavengine.app.Methods.files;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Image extends PavWidget {
    TextureRegion image;

    public Image(String preview) {
        this.image = new TextureRegion(new Texture(files(preview)));
        type = WidgetType.TextBox;
        box = new Rectangle(0, 0, 32, 32);
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(box.x + box.width / 2f, box.y + box.height / 2f);
    }

    @Override
    public boolean clicked(Vector2 point) {
        return false;
    }

    @Override
    public void update() {

    }

    @Override
    public void render(SpriteBatch sb) {
        sb.draw(image, box.x, box.y, box.width, box.height);
    }

    @Override
    public void setSize(float spriteWidth, float spriteHeight) {
        box.setSize(spriteWidth, spriteHeight);
        if (background != null) background.setSize(spriteWidth, spriteHeight);

    }

    @Override
    public void setPosition(float x, float y) {
        box.setPosition(x, y);
        if (background != null) background.setPosition(x, y);
    }
}
