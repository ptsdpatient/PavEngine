package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public abstract class CinematicPanelWidget {

    public TextureRegion texture;
    public String name;
    BitmapFont font;
    float x, y;
    GlyphLayout layout;
    public CinematicWidgetType type;
    public Rectangle bound;

    public CinematicPanelWidget(String name, Vector2 position, TextureRegion texture, CinematicWidgetType type) {
        this.texture = uiBG[8];
        this.name = name;
        this.font = gameFont[1];
        this.x = position.x;
        this.y = position.y;
        this.type = type;
        layout = new GlyphLayout(font, name, Color.WHITE,224, Align.center,true);
        bound = new Rectangle(0, y,224,40);
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        bound.setPosition(x,y);
    }
    public Vector2 getPosition() {
        return new Vector2(x,y);
    }

    public void draw(SpriteBatch sb, float scrollY) {
        float drawY = y + scrollY;
        bound.setPosition(x, drawY - 32);
        sb.draw(texture, x, drawY - 32, 224, 40);

        font.draw(sb, layout, x, drawY);
    }
}
