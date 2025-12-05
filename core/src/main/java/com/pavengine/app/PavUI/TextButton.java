package com.pavengine.app.PavUI;

import static com.pavengine.app.Debug.Draw.debugRectangle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.AnimationCurve;

public class TextButton extends PavWidget {
    GlyphLayout layout;
    TextureRegion hoverTexture;
    AnimationCurve curve = AnimationCurve.EASE_IN_OUT;

    public TextButton(String text, BitmapFont font, ClickBehavior clickBehavior) {
        this.text = text;
        this.font = font;

        this.clickBehavior = clickBehavior;

        layout = new GlyphLayout(font, text);

        this.box = new Rectangle(0, 0, layout.width, layout.height);
        type = WidgetType.TextButton;

    }

    public TextButton(String text, BitmapFont font, TextureRegion hoverTexture, TextureRegion background, ClickBehavior clickBehavior) {
        this.text = text;
        this.font = font;
        this.clickBehavior = clickBehavior;
        layout = new GlyphLayout(font, text);
        this.hoverTexture = hoverTexture;
        this.background = new Sprite(background);
        this.box = new Rectangle(0, 0, layout.width, layout.height);
        type = WidgetType.TextButton;
    }

    public TextButton(String model, BitmapFont font, TextureRegion textureRegion, ClickBehavior clickBehavior) {
        this.text = model;
        this.font = font;
        this.clickBehavior = clickBehavior;
        layout = new GlyphLayout(font, text);
        this.background = new Sprite(textureRegion);
        this.box = new Rectangle(0, 0, layout.width, layout.height);
        type = WidgetType.TextButton;
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(box.x + box.width / 2f, box.y + box.height / 2f);
    }

    @Override
    public boolean clicked(Vector2 point) {
        return box.contains(point);
    }

    @Override
    public void update() {
        // Placeholder: you could add animations, scaling, etc.
        if (background != null) background.setBounds(box.x, box.y, box.width, box.height);
    }


    @Override
    public void render(SpriteBatch sb) {
        if (background != null) background.draw(sb);



        layout = new GlyphLayout(font, text);
        float textX = box.x + (box.width - layout.width) / 2f;
        float textY = box.y + (box.height + layout.height) / 2f;

        if (isHovered && hoverTexture != null) {
            sb.draw(hoverTexture, box.x, box.y, box.width, box.height);
        }

        font.draw(sb, layout, textX, textY);

    }

    @Override
    public void setSize(float spriteWidth, float spriteHeight) {
        box.setSize(Math.max(layout.width, spriteWidth), Math.max(layout.height, spriteHeight));
        if (background != null) background.setSize(Math.max(layout.width, spriteWidth), Math.max(layout.height, spriteHeight));
    }

    @Override
    public void setPosition(float x, float y) {
        box.setPosition(x, y);
        if (background != null) background.setPosition(x, y);
    }
}
