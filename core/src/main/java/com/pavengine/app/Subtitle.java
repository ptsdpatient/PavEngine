package com.pavengine.app;

import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

public class Subtitle {

    private BitmapFont font;
    private float wrapWidth;
    private float x, y;


    public Subtitle(BitmapFont font, float screenWidth, float screenHeight) {
        this.font = font;
        this.wrapWidth = screenWidth * 0.8f;
        this.font.getData().markupEnabled = true;
        x = (screenWidth - wrapWidth) / 2f;
        y = screenHeight * 0.18f;
    }

    public void draw(SpriteBatch batch, GlyphLayout layout) {
        font.draw(batch, layout, x, y);
    }
}
