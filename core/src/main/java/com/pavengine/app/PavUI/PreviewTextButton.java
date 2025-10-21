package com.pavengine.app.PavUI;

import static com.pavengine.app.Methods.files;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.AnimationCurve;

public class PreviewTextButton extends PavWidget {
    GlyphLayout layout = new GlyphLayout();
    TextureRegion hoverTexture = new TextureRegion();
    AnimationCurve curve = AnimationCurve.EASE_IN_OUT;
    TextureRegion preview = new TextureRegion();
    float previewHeight = 80f;

    public PreviewTextButton(String text, BitmapFont font, ClickBehavior clickBehavior) {
        this.text = text;
        this.font = font;

        this.clickBehavior = clickBehavior;

        layout = new GlyphLayout(font, text);

        this.box = new Rectangle(0, 0, layout.width, layout.height);

        type = WidgetType.TextBox;
    }

    public PreviewTextButton(String text, String preview, BitmapFont font, TextureRegion hoverTexture, ClickBehavior clickBehavior) {
        this.text = text;
        this.font = font;
        this.preview = new TextureRegion(new Texture(files(preview)));
        this.clickBehavior = clickBehavior;
        layout = new GlyphLayout(font, text);
        this.hoverTexture = hoverTexture;
        this.box = new Rectangle(0, 0, layout.width, layout.height);
        type = WidgetType.TextBox;
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
        if (background != null) background.setBounds(box.x, box.y, box.width, box.height);
    }


    @Override
    public void render(SpriteBatch sb) {
        if (background != null) background.draw(sb);


        layout = new GlyphLayout(font, text);
        float textX = box.x + (box.width - layout.width) / 2f;
        float textY = box.y + layout.height;

        if (isHovered) {
            sb.draw(hoverTexture, box.x, box.y, box.width, box.height);
        }
        if (preview != null) {
            sb.draw(preview, box.x, textY, box.width, box.height - 20);
        }
        font.draw(sb, layout, textX, textY);
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

