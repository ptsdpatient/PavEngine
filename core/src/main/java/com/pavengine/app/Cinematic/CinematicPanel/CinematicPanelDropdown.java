package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavScreen.BoundsEditor.selectedBound;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavBounds.PavBoundsType;
import com.pavengine.app.PavUI.WidgetType;

public class CinematicPanelDropdown {

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
    BitmapFont font;

    private CinematicWidgetType[] list = new CinematicWidgetType[]{
        CinematicWidgetType.Animate,
        CinematicWidgetType.Camera,
        CinematicWidgetType.Music,
        CinematicWidgetType.Subtitle
    };

    Sprite background;
    private GlyphLayout[] optionLayouts;
    private Rectangle[] optionRects;
    private Rectangle box;

    public CinematicPanelDropdown(TextureRegion background, TextureRegion hover, Vector2 position) {
        this.font = gameFont[2];
        this.background = new Sprite(background);
        this.hoverTexture = hover;

        buttonLayout = new GlyphLayout(font, CinematicWidgetType.Subtitle.name());
        this.box = new Rectangle(0, 0, buttonLayout.width + 40, OPTION_HEIGHT);

        buttonRect = new Rectangle();

        optionLayouts = new GlyphLayout[list.length];
        optionRects = new Rectangle[list.length];
        for (int i = 0; i < list.length; i++) {
            optionLayouts[i] = new GlyphLayout(font, list[i].name());
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
        // Main button
        background.setBounds(buttonRect.x  , buttonRect.y, buttonRect.width, buttonRect.height);
        background.draw(batch);
        font.draw(batch, cinematicPanel.currentWidgetType.name(), buttonRect.x + 10, buttonRect.y + OPTION_HEIGHT / 1.4f);

        if (!dropDownExpand) return;

        // Dropdown box
        for (int i = 0; i < list.length; i++) {
            Rectangle rect = optionRects[i];
            if (rect.y + rect.height < buttonRect.y - visibleHeight || rect.y > buttonRect.y)
                continue;
            batch.draw(cursor.clicked(rect)?hoverTexture:background, rect.x, rect.y, rect.width, rect.height);

//            if () {
//                buttonHovered = true;
//            } else {
//                buttonHovered = false;
//            }

            font.draw(batch, list[i].name(), rect.x + 10, rect.y + OPTION_HEIGHT / 1.4f);
        }
//        for(Rectangle rect : optionRects) {
//            debugRectangle(rect, Color.ORANGE);
//        }
    }

    public void click() {
        if(cursor.clicked(buttonRect)) {
            dropDownExpand = !dropDownExpand;
            return;
        } else dropDownExpand = false;

        for (int i = 0; i < list.length; i++) {
            if (cursor.clicked(optionRects[i])) {
                cinematicPanel.currentWidgetType = list[i];
                buttonLayout.setText(font, cinematicPanel.currentWidgetType.name());
                dropDownExpand = false;
                break;
            }
        }
    }

    public void scroll(float amountY) {
        if (!dropDownExpand) return;
        scrollOffset += amountY * 10f;
        scrollOffset = Math.min(0f, Math.max(scrollOffset, -(list.length * OPTION_HEIGHT - visibleHeight)));
    }
}
