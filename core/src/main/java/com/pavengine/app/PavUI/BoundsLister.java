package com.pavengine.app.PavUI;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class BoundsLister extends PavWidget{
    public BitmapFont font;
    public Rectangle box;
    public boolean expanded = false;
    public WidgetType type;
    GlyphLayout layout;
    float spriteWidth = 192, spriteHeight = 48;
    Sprite background;
    private Vector3 overlayTouch = new Vector3();

    public BoundsLister(BitmapFont font) {
        this.font = font;

        layout = new GlyphLayout(font, "");
        this.box = new Rectangle(0, 0, spriteWidth, spriteHeight);

        if (background != null) {
            background.setPosition(0, 0);
            background.setSize(spriteWidth, spriteHeight);
        }
        type = WidgetType.TextBox;
    }

    @Override
    public Vector2 getCenter() {
        return null;
    }

    public boolean clicked(Vector2 point) {
        if (box.contains(point)) {
            expanded = !expanded;
            return false;
        }
        return false;
    }

    public void update() {
        if (background != null) {
            background.setBounds(box.x, box.y, box.width, box.height);
        }
    }

    public void render(SpriteBatch sb) {
        // Draw main box
        if (background != null) background.draw(sb);

        float textX = box.x + 10;
        float textY = box.y + (box.height + layout.height) / 2f;
        font.draw(sb, layout, textX, textY);

        for (int i =0; i< selectedObject.boxes.size; i++) {
            float optY = box.y + (i + 1) * spriteHeight;
            Rectangle optBox = new Rectangle(box.x, optY, spriteWidth, spriteHeight);
            if (background != null) {
                background.setBounds(optBox.x, optBox.y, optBox.width, optBox.height);
                background.draw(sb);
            }
            layout.setText(font, selectedObject.boxes.get(i).type.name());
            font.draw(sb, layout, textX, optY + (spriteHeight + layout.height) / 2f);
        }

        // Handle input
        overlayTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        overlayViewport.unproject(overlayTouch);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clicked(new Vector2(overlayTouch.x, overlayTouch.y));
        }
    }

    @Override
    public void setSize(float spriteWidth, float spriteHeight) {

    }

    @Override
    public void setPosition(float x, float y) {

    }

}
