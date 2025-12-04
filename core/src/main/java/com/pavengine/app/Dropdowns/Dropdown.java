package com.pavengine.app.Dropdowns;

import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public abstract class Dropdown {

    public GlyphLayout buttonLayout;
    private TextureRegion hoverTexture;
    public Rectangle buttonRect;
    public boolean dropDownExpand = false;
    private float visibleHeight = 275f;

    private static final float OPTION_HEIGHT = 48f;
    private static final float OPTION_GAP = 4f;
    BitmapFont font;

    Sprite background;
    private GlyphLayout[] optionLayouts;
    private Rectangle[] optionRects;
    public Rectangle box;
    public String[] list;

    public Dropdown(TextureRegion background, TextureRegion hover, Vector2 position, String[] list) {
        this.list = list;
        this.font = gameFont[1];
        this.background = new Sprite(background);
        this.hoverTexture = hover;
        buttonLayout = new GlyphLayout(font, list[0]);
        this.box = new Rectangle(0, 0, buttonLayout.width + 40, OPTION_HEIGHT);

        buttonRect = new Rectangle();

        optionLayouts = new GlyphLayout[list.length];
        optionRects = new Rectangle[list.length];
        for (int i = 0; i < list.length; i++) {
            optionLayouts[i] = new GlyphLayout(font, list[i]);
            optionRects[i] = new Rectangle();
        }
        setPosition(position.x,position.y);
    }

    public void setPosition(float x, float y) {
        buttonRect.set(x, y, box.width, box.height);

        float dropX = x;
        float dropY = y - OPTION_HEIGHT - 6;
        for (int i = 0; i < list.length; i++) {
            optionRects[i].set(dropX, dropY - (OPTION_HEIGHT + OPTION_GAP) * i, box.width, OPTION_HEIGHT);
        }
    }

    public void draw(SpriteBatch batch) {

        background.setBounds(buttonRect.x  , buttonRect.y, buttonRect.width, buttonRect.height);
        background.draw(batch);
        font.draw(batch, buttonLayout, buttonRect.x, buttonRect.y + OPTION_HEIGHT / 1.4f);

        if (!dropDownExpand) return;

        for (int i = 0; i < list.length; i++) {
            Rectangle rect = optionRects[i];
            if (rect.y + rect.height < buttonRect.y - visibleHeight || rect.y > buttonRect.y)
                continue;
            batch.draw(cursor.clicked(rect)?hoverTexture:background, rect.x, rect.y, rect.width, rect.height);
            font.draw(batch, list[i], rect.x + 10, rect.y + OPTION_HEIGHT / 1.4f);
        }
    }

    public void click() {

        for (int i = 0; i < list.length; i++) {
            if (cursor.clicked(optionRects[i]) && dropDownExpand) {
                optionClicked(i);
                dropDownExpand = false;
                break;
            }
        }

        if(cursor.clicked(buttonRect)) {
            dropDownExpand = !dropDownExpand;
        } else dropDownExpand = false;
    }

    abstract void optionClicked(int i);

}
