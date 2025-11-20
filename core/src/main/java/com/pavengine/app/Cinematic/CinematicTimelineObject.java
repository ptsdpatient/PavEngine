package com.pavengine.app.Cinematic;

import static com.pavengine.app.PavEngine.resolution;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;

public class CinematicTimelineObject {

    TextureRegion nameTexture;
    TextureRegion lineTexture;
    String name;
    BitmapFont font;
    float x = 0, y = 50;
    GlyphLayout layout;

    public CinematicTimelineObject(String name, BitmapFont fnt,
                                   TextureRegion nameTexture, TextureRegion lineTexture) {
        this.nameTexture = nameTexture;
        this.lineTexture = lineTexture;
        this.name = name;
        this.font = fnt;
        layout = new GlyphLayout(font, name, Color.BLACK,196, Align.center,true);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void draw(SpriteBatch sb, float scrollX, float scrollY) {

        float drawY = y + scrollY;

        float lineStart = 196 + 50;
        sb.draw(lineTexture, lineStart, drawY - 16, resolution.x - lineStart - 10, 4);

        sb.draw(nameTexture, x, drawY - 32, 196, 40);
        font.draw(sb, name, x + 10, drawY);

    }
}
