package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.hoverUIBG;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavEngine.uiControl;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class CinematicModal implements InputProcessor {
    TextureRegion bg;
    TextureRegion closeBG;

    TextureRegion widgetBG;
    static TextureRegion accentBG;
    TextureRegion deleteBG;
    public Rectangle closeBound = new Rectangle(),deleteBound = new Rectangle();

    public CinematicModal() {
        this.bg = uiBG[1];
        this.closeBG = uiControl[5];
        this.widgetBG = uiBG[0];
        accentBG = hoverUIBG[2];
        deleteBG = uiControl[6];
        closeBound.set(resolution.x-60,resolution.y-60,48,48);
        deleteBound.set(resolution.x-60,resolution.y-60 - 54,48,48);
        Gdx.input.setInputProcessor(this);
    }

    public void save(){};

    public abstract void draw(SpriteBatch sb);

    public Rectangle[] getDebugRect() {
        return null;
    }

    public void render(SpriteBatch sb) {
        sb.draw(bg,0,0,resolution.x,resolution.y);
        sb.draw(closeBG, closeBound.x, closeBound.y,48,48);
        sb.draw(deleteBG, deleteBound.x, deleteBound.y,48,48);
        draw(sb);
    }

    public void debug(SpriteBatch sb) {
        debugRectangle(deleteBound, Color.CYAN);
    }
}
