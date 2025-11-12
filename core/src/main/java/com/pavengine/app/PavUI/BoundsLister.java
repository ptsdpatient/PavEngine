package com.pavengine.app.PavUI;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavScreen.BoundsEditor.bounds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavBounds.PavBoundsType;

public class BoundsLister extends PavWidget {
    private GlyphLayout buttonLayout;
    private TextureRegion hoverTexture;
    public Rectangle buttonRect;
    public boolean dropDownExpand = false;
    public boolean buttonHovered = false;
    public float scrollOffset = 0f;
    private float visibleHeight = 275f;
    private float itemHeight = 48f;
    private static final float OPTION_HEIGHT = 48f;
    private static final float OPTION_GAP = 4f;

    private PavBoundsType[] list = new PavBoundsType[]{
        PavBoundsType.Bound,
        PavBoundsType.Ground,
        PavBoundsType.HurtBounds,
        PavBoundsType.Enterance
    };

    private GlyphLayout[] optionLayouts;
    private Rectangle[] optionRects;

    public BoundsLister(BitmapFont font, TextureRegion background, TextureRegion hover) {
        this.font = font;
        this.text = "New Bounds";
        this.background = new Sprite(background);
        this.hoverTexture = hover;
        this.type = WidgetType.TextButton;

        buttonLayout = new GlyphLayout(font, text);
        this.box = new Rectangle(0, 0, buttonLayout.width + 40, OPTION_HEIGHT);

        buttonRect = new Rectangle();

        optionLayouts = new GlyphLayout[list.length];
        optionRects = new Rectangle[list.length];
        for (int i = 0; i < list.length; i++) {
            optionLayouts[i] = new GlyphLayout(font, list[i].name());
            optionRects[i] = new Rectangle();
        }
    }

    @Override
    public void setPosition(float x, float y) {
        box.setPosition(x, y);
        if (background != null) background.setPosition(x, y);
        updateOptionRects();
    }

    @Override
    public void setSize(float width, float height) {
        box.setSize(width, height);
        if (background != null) background.setSize(width, height);
        updateOptionRects();
    }

    private void updateOptionRects() {
        float startY = box.y + box.height - 38 - OPTION_HEIGHT - OPTION_GAP;
        for (int i = 0; i < list.length; i++) {
            optionRects[i].set(box.x, startY - (OPTION_HEIGHT + OPTION_GAP) * i, box.width, OPTION_HEIGHT);
        }
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(box.x + box.width / 2f, box.y + box.height / 2f);
    }

    @Override
    public void update() {
        if (background != null) background.setBounds(box.x, box.y, box.width, box.height);
    }

    @Override
    public boolean clicked(Vector2 point) {
        if (box.contains(point)) {
            dropDownExpand = !dropDownExpand;
            return true;
        }
        if (dropDownExpand) {
            for (int i = 0; i < list.length; i++) {
                if (optionRects[i].contains(point)) {
                    System.out.println("Selected: " + list[i]);
                    dropDownExpand = false;
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void render(SpriteBatch sb) {
        // Draw main button
        if (background != null) background.draw(sb);
        buttonRect.set(box.x, box.y + box.height - 42, box.width, 48);
        if (buttonHovered) {
            sb.draw(hoverTexture, buttonRect.x, buttonRect.y, buttonRect.width, buttonRect.height);
        }

        font.draw(sb, buttonLayout, box.x + (box.width - buttonLayout.width) * 0.5f, buttonRect.y + buttonRect.height - 12);


        float maxScroll = Math.max(0, bounds.size * itemHeight - visibleHeight);
        scrollOffset = Math.max(0, Math.min(scrollOffset, maxScroll));

        if (dropDownExpand) {
            for (int i = 0; i < list.length; i++) {
                Rectangle rect = optionRects[i];
                GlyphLayout layout = optionLayouts[i];

                if (cursor.clicked(rect)) {
                    sb.draw(hoverTexture, rect.x, rect.y, rect.width, rect.height);
                    if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                        bounds.add(new PavBounds(list[i]));
                        dropDownExpand = false;
                    }
                }

                float optionTextX = rect.x + (rect.width - layout.width) / 2f;
                float optionTextY = rect.y + (rect.height + layout.height) / 2f - 4;
                font.draw(sb, layout, optionTextX, optionTextY);
            }
        } else {

            float visibleTop = box.y + box.height - 40f;
            float visibleBottom = box.y + 10f;

            float startY = visibleTop - itemHeight;
            int startIndex = (int)(scrollOffset / itemHeight);
            int endIndex = Math.min(bounds.size, startIndex + (int)(visibleHeight / itemHeight) + 1);

            for (int i = startIndex; i < endIndex; i++) {
                float y = startY - (i * itemHeight - scrollOffset);

                if (y + itemHeight < visibleBottom || y > visibleTop) continue;

                // Temporary rectangle for hover & click check
                Rectangle rect = new Rectangle(box.x, y, box.width, itemHeight);

                PavBounds b = bounds.get(i);
                String label = (i + 1) + ". " + b.type.name();

                // Hover effect
                boolean hovered = cursor.clicked(rect);
                if (hovered) sb.draw(hoverTexture, rect.x, rect.y, rect.width, rect.height);

                // Click detection
                if (hovered && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    dropDownExpand = false;
                    System.out.println("Clicked bound: " + label);
                }

                GlyphLayout layout = new GlyphLayout(font, label);
                float textX = rect.x + (rect.width - layout.width) / 2f;
                float textY = rect.y + (rect.height + layout.height) / 2f - 4;
                font.draw(sb, layout, textX, textY);
            }

        }
    }
}
