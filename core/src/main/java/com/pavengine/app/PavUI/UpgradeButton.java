package com.pavengine.app.PavUI;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.AnimationCurve;

public class UpgradeButton extends PavWidget {
    GlyphLayout layout;
    TextureRegion hoverTexture = new TextureRegion();
    AnimationCurve curve = AnimationCurve.EASE_IN_OUT;
    Sprite increase = new Sprite();
    int cost = 0;
    TextureRegion empty, done;
    int value = 0;

    public UpgradeButton(String text, int cost, BitmapFont font, ClickBehavior clickBehavior, TextureRegion buttonTexture, TextureRegion done, TextureRegion empty) {
        this.text = text;
        this.font = font;
        this.value = 0;
        this.cost = cost;
        this.clickBehavior = clickBehavior;

        layout = new GlyphLayout(font, text);

        this.box = new Rectangle(0, 0, layout.width, layout.height);
        type = WidgetType.TextButton;
        this.increase = new Sprite(buttonTexture);
        increase.setPosition(50, 50);

        this.empty = empty;
        this.done = done;
    }

    public UpgradeButton(String text, BitmapFont font, TextureRegion hoverTexture, TextureRegion background, ClickBehavior clickBehavior) {
        this.text = text;
        this.font = font;
        this.clickBehavior = clickBehavior;
        layout = new GlyphLayout(font, text);
        this.hoverTexture = hoverTexture;
        this.background = new Sprite(background);
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


        layout = new GlyphLayout(font, text + " (" + cost + "/-)");
        float textX = box.x + (box.width - layout.width) / 2f;
        float textY = box.y + (box.height + layout.height) / 2f;

        if (isHovered) {
            sb.draw(hoverTexture, box.x, box.y, box.width, box.height);
        }

        font.draw(sb, layout, textX, textY);
        for (int i = 0; i < 5; i++) {
            sb.draw(upgradeIndex - 1 < i ? empty : done, textX + layout.width + 12 + i * 35, textY - 32);
        }
        increase.setPosition(textX + layout.width + 12 + 5 * 35, textY - increase.getHeight());
        increase.draw(sb);


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
