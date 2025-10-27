package com.pavengine.app.PavUI;

import static com.pavengine.app.PavEngine.enableMapEditor;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Checkbox {
    public BitmapFont font, subtitle;
    public String text;
    public Rectangle box;
    public boolean isHovered = false;
    public ClickBehavior clickBehavior = ClickBehavior.Nothing;
    public boolean value = false;
    public WidgetType type;
    GlyphLayout layout;
    float spriteWidth = 192, spriteHeight = 64;
    Sprite background, checkBox;
    TextureRegion trueTexture;
    TextureRegion falseTexture;
    private Vector3 overlayTouch = new Vector3();

    public Checkbox(float x, float y, boolean value, ClickBehavior clickBehavior, String text, BitmapFont font, TextureRegion trueTexture, TextureRegion falseTexture) {
        this.text = text;
        this.font = font;
        layout = new GlyphLayout(font, text);
        this.value = value;
        this.box = new Rectangle(0, 0, layout.width, layout.height);

        this.clickBehavior = clickBehavior;

        box.setPosition(x, y);

        if (background != null) background.setPosition(x, y);

        box.setSize(spriteWidth, spriteHeight);
        if (background != null) background.setSize(spriteWidth, spriteHeight);

        this.trueTexture = trueTexture;
        this.falseTexture = falseTexture;
        checkBox = new Sprite(this.trueTexture);
        checkBox.setPosition(box.x + box.width - 40, box.y + (box.height - 40) / 2f);
        checkBox.setSize(40, 40);

        type = WidgetType.TextBox;
    }

    public Vector2 getCenter() {
        return new Vector2(box.x + box.width / 2f, box.y + box.height / 2f);
    }

    public boolean clicked(Vector2 point) {
        return box.contains(point);
    }


    public void update() {
        // Placeholder: you could add animations, scaling, etc.
        if (background != null) background.setBounds(box.x, box.y, box.width, box.height);
    }


    public void render(SpriteBatch sb) {
        if (background != null) background.draw(sb);

        layout = new GlyphLayout(font, text);
        float textX = box.x + 10;
        float textY = box.y + (box.height + layout.height) / 2f;

        font.draw(sb, layout, textX, textY);
        checkBox.draw(sb);
//        sb.draw(value?trueTexture:falseTexture,box.x+box.width-40,box.y,40,40);

        overlayTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        overlayViewport.unproject(overlayTouch);
        if (enableMapEditor) {
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (selectedObject == null) return;
                switch (clickBehavior) {
                    case CheckboxRoom: {
                        if (checkBox.getBoundingRectangle().contains(overlayTouch.x, overlayTouch.y)) {
                            value = !value;
                            checkBox = new Sprite(value ? trueTexture : falseTexture);
                            checkBox.setPosition(box.x + box.width - 40, box.y + (box.height - 40) / 2f);
                            checkBox.setSize(40, 40);
                        }
                    }
                    ;
                    break;

                }
            }
        }

    }


}
