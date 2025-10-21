package com.pavengine.app.PavUI;

import static com.pavengine.app.PavEngine.sprayLimit;
import static com.pavengine.app.PavEngine.sprayTime;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class SprayBar extends PavWidget {
    public float displayedSprayProgress = 0f;
    TextureRegion hoverTexture = new TextureRegion();
    TextureRegion sprayBG, sprayFG;

    public SprayBar(String text, BitmapFont font, ClickBehavior clickBehavior, TextureRegion sprayFG, TextureRegion sprayBG) {
        this.text = text;
        this.font = font;
        this.clickBehavior = clickBehavior;


        type = WidgetType.TextButton;


        this.sprayBG = sprayBG;
        this.sprayFG = sprayFG;
        box = new Rectangle(0, 0, 192, 32);
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

        float textX = box.x + (box.width) / 2f;
        float textY = box.y + (box.height) / 2f;

        if (isHovered) {
            sb.draw(hoverTexture, box.x, box.y, box.width, box.height);
        }


//        sb.draw(sprayBG, textX, textY , 192, 32);
//
//        float sprayProgress = MathUtils.clamp((sprayLimit - sprayTime) / sprayLimit, 0f, 1f);
//
//        sb.draw(sprayFG, textX, textY , 192 * sprayProgress, 32);


        // Calculate target progress (0 → 1)
        float sprayProgress = MathUtils.clamp((sprayLimit - sprayTime) / sprayLimit, 0f, 1f);

// Smoothly interpolate displayed value
        displayedSprayProgress = MathUtils.lerp(displayedSprayProgress, sprayProgress, 10f * Gdx.graphics.getDeltaTime());

// Draw background
        sb.draw(sprayBG, textX, textY, 192, 32);

// Draw smooth foreground
        sb.draw(sprayFG, textX, textY, 192 * displayedSprayProgress, 32);
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
