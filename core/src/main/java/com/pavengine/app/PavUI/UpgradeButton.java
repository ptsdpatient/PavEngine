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
    private GlyphLayout layout;
    private TextureRegion hoverTexture;
    private AnimationCurve curve = AnimationCurve.EASE_IN_OUT;
    private Sprite increase;
    private int cost;
    private TextureRegion empty, done;
    private int value;
    private float padding = 12;
    private float iconSpacing = 35;
    private int numIcons = 5;

    public UpgradeButton(String text, int cost, BitmapFont font, ClickBehavior clickBehavior,
                         TextureRegion buttonTexture, TextureRegion done, TextureRegion empty) {
        this.text = text;
        this.font = font;
        this.cost = cost;
        this.clickBehavior = clickBehavior;
        this.done = done;
        this.empty = empty;

        this.increase = new Sprite(buttonTexture);
        this.layout = new GlyphLayout(font, text + " (" + cost + "/-)");
        this.type = WidgetType.TextButton;

        recalcBox();
    }

    public UpgradeButton(String text, BitmapFont font, TextureRegion hoverTexture,
                         TextureRegion background, ClickBehavior clickBehavior) {
        this.text = text;
        this.font = font;
        this.hoverTexture = hoverTexture;
        this.clickBehavior = clickBehavior;
        this.background = new Sprite(background);
        this.layout = new GlyphLayout(font, text);
        this.type = WidgetType.TextButton;

        recalcBox();
    }

    /** Compute box size that includes text, icons, and button */
    private void recalcBox() {
        float textW = layout.width;
        float textH = layout.height;
        float iconsWidth = numIcons * iconSpacing;
        float buttonWidth = increase != null ? increase.getWidth() : 0;
        float height = Math.max(textH + 20, done != null ? done.getRegionHeight() + 10 : 0);

        // box = text + icons + increase + padding
        float width = textW + padding + iconsWidth + padding + buttonWidth;
        this.box = new Rectangle(0, 0, width, height);
    }

    @Override
    public void update() {
        if (background != null)
            background.setBounds(box.x, box.y, box.width, box.height);
    }

    @Override
    public void render(SpriteBatch sb) {
        if (background != null)
            background.draw(sb);

        layout.setText(font, text + " (" + cost + "/-)");
        float textX = box.x + padding;
        float textY = box.y + (box.height + layout.height) / 2f;

        if (isHovered && hoverTexture != null)
            sb.draw(hoverTexture, box.x, box.y, box.width, box.height);

        font.draw(sb, layout, textX, textY);

        // Draw icons after text
        float iconStartX = textX + layout.width + padding;
        for (int i = 0; i < numIcons; i++) {
            TextureRegion icon = (value - 1 < i) ? empty : done;
            sb.draw(icon, iconStartX + i * iconSpacing, box.y + (box.height - icon.getRegionHeight()) / 2f);
        }

        // Draw the increase button at the end
        if (increase != null) {
            float incX = iconStartX + numIcons * iconSpacing + padding;
            float incY = box.y + (box.height - increase.getHeight()) / 2f;
            increase.setPosition(incX, incY);
            increase.draw(sb);
        }
    }

    @Override
    public void setSize(float width, float height) {
        box.setSize(width, height);
        if (background != null)
            background.setSize(width, height);
    }

    @Override
    public void setPosition(float x, float y) {
        box.setPosition(x, y);
        if (background != null)
            background.setPosition(x, y);
    }

    @Override
    public boolean clicked(Vector2 point) {
        return box.contains(point);
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(box.x + box.width / 2f, box.y + box.height / 2f);
    }
}
