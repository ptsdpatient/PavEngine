package com.pavengine.app.PavUI;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class PavWidget {

    public BitmapFont font, subtitle;
    public String text;
    public Rectangle box;
    public Sprite background, image;
    public boolean isHovered = false;
    public ClickBehavior clickBehavior = ClickBehavior.Nothing;
    public WidgetType type = WidgetType.Undefined;
    public int upgradeIndex = 1;
    GlyphLayout layout = new GlyphLayout();

    public abstract Vector2 getCenter();

    public abstract boolean clicked(Vector2 point);

    public abstract void update();

    public abstract void render(SpriteBatch sb);

    public abstract void setSize(float spriteWidth, float spriteHeight);

    public abstract void setPosition(float x, float y);
}
