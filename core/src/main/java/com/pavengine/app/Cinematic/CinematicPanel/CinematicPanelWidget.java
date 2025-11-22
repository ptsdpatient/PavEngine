package com.pavengine.app.Cinematic.CinematicPanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

public abstract class CinematicPanelWidget {

    TextureRegion nameTexture;
    String name;
    BitmapFont font;
    float x = 0, y = 50;
    GlyphLayout layout;

    public CinematicPanelWidget(String name, BitmapFont fnt, TextureRegion nameTexture) {
        this.nameTexture = nameTexture;
        this.name = name;
        this.font = fnt;
        layout = new GlyphLayout(font, name, Color.BLACK,196, Align.center,true);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(SpriteBatch sb, float scrollY) {
        float drawY = y + scrollY;
        sb.draw(nameTexture, x, drawY - 32, 196, 40);
        font.draw(sb, name, x + 10, drawY);
    }
}
