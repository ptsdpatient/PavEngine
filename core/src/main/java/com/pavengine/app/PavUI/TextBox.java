package com.pavengine.app.PavUI;

import static com.pavengine.app.Methods.isKeyJustPressed;
import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public class TextBox extends PavWidget {
    public ArrayList<String> messages = new ArrayList<>();
    public String current = "";
    GlyphLayout layout;
    boolean typeWriter = true;
    float timer = 0f;
    float typeRate = 0.05f;
    int textIndex = 0;

    public TextBox(String text, BitmapFont font, ClickBehavior clickBehavior) {
        this.text = text;
        this.font = font;

        this.clickBehavior = clickBehavior;

        layout = new GlyphLayout(font, text);

        this.box = new Rectangle(0, 0, layout.width, layout.height);

        type = WidgetType.TextBox;


    }

    public TextBox(String text, BitmapFont font, TextureRegion background, ClickBehavior clickBehavior) {
        this.text = text;
        this.font = font;
        this.clickBehavior = clickBehavior;
        layout = new GlyphLayout(font, text);
        this.background = new Sprite(background);
        this.box = new Rectangle(0, 0, layout.width, layout.height);
        type = WidgetType.TextBox;
    }

    public void addMessage(String text) {
        textIndex = 0;
        timer = 0f;
        messages.add(text);
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

        if (!messages.isEmpty()) {

            // Draw background (optional)
            if (background != null) background.draw(sb);

            current = messages.get(0);

            if (typeWriter) {
                timer += Gdx.graphics.getDeltaTime();
                if (timer >= typeRate) {
                    timer = 0f;
                    textIndex = Math.min(textIndex + 1, current.length());
                }
            } else {
                textIndex = current.length();
            }

            String visibleText = current.substring(0, Math.min(textIndex, current.length()));

            layout.setText(font, visibleText, Color.WHITE, box.width, Align.left, true);

            float textX = box.x + (box.width - layout.width) / 2f;
            float textY = box.y + (box.height + layout.height) / 2f;

            font.draw(sb, layout, textX, textY);

            // Input: advance text or message
            if (isKeyJustPressed("E")) {
                if (textIndex < current.length()) {
                    // Skip typing and show full message
                    textIndex = current.length();
                } else {
                    // Move to next message
                    current = "";
                    messages.remove(0);
                    textIndex = 0;   // ✅ reset for next message
                    timer = 0f;
                }
            }
        }


//        if(isHovered) {
//            sb.draw(background,box.x,box.y,box.width,box.height);
//        }
    }

    @Override
    public void setSize(float spriteWidth, float spriteHeight) {
        print(spriteHeight + " : " + spriteWidth);
        box.setSize(spriteWidth, spriteHeight);
        if (background != null) background.setSize(spriteWidth, spriteHeight);
    }

    @Override
    public void setPosition(float x, float y) {
        box.setPosition(x, y);
        if (background != null) background.setPosition(x, y);
    }
}
