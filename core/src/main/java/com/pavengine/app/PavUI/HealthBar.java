package com.pavengine.app.PavUI;

import static com.pavengine.app.PavEngine.health;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class HealthBar extends PavWidget {
    GlyphLayout layout;
    TextureRegion hoverTexture = new TextureRegion();
    float displayedHealth = 100f; // start full

    TextureRegion healthBG, healthFG;

    public HealthBar(String text, BitmapFont font, ClickBehavior clickBehavior, TextureRegion healthFG, TextureRegion healthBG) {
        this.text = text;
        this.font = font;
        this.clickBehavior = clickBehavior;

        layout = new GlyphLayout(font, text);

        this.box = new Rectangle(0, 0, layout.width, layout.height);
        type = WidgetType.TextButton;


        this.healthBG = healthBG;
        this.healthFG = healthFG;
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


        layout = new GlyphLayout(font, text + "  ");
        float textX = box.x + (box.width - layout.width) / 2f;
        float textY = box.y + (box.height + layout.height) / 2f;

        if (isHovered) {
            sb.draw(hoverTexture, box.x, box.y, box.width, box.height);
        }

        font.draw(sb, layout, textX, textY);

//        sb.draw(healthBG,textX + layout.width, textY-32 + 4,192,32);
//        sb.draw(healthFG,textX + layout.width, textY-32 + 4,health*192/100,32);
// Smooth transition toward actual health
        displayedHealth = MathUtils.lerp(displayedHealth, health, 10f * Gdx.graphics.getDeltaTime());

// Draw background
        sb.draw(healthBG, textX + layout.width, textY - 32 + 4, 192, 32);

// Draw smooth foreground
        sb.draw(healthFG, textX + layout.width, textY - 32 + 4, (displayedHealth / 100f) * 192f, 32);

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
