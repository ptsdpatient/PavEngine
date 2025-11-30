package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavEngine.uiControl;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public abstract class CinematicModal implements InputProcessor {
    TextureRegion bg,closeBG,doneBG,widgetBG,accentBG;
    public Rectangle closeBound = new Rectangle();
    public Rectangle doneBound = new Rectangle();

    public CinematicModal() {
        this.bg = uiBG[2];
        this.closeBG = uiControl[5];
        this.doneBG = uiControl[4];
        this.widgetBG = uiBG[0];
        this.accentBG = uiBG[3];
        closeBound.set(resolution.x-60,resolution.y-60,32,32);
        doneBound.set(resolution.x-60,60,32,32);
        Gdx.input.setInputProcessor(this);
    }

    public abstract void draw(SpriteBatch sb);

    public Rectangle[] getDebugRect() { return null; }

    public void render(SpriteBatch sb) {
        sb.draw(bg,0,0,resolution.x,resolution.y);
        sb.draw(closeBG,closeBound.x,closeBound.y,32,32);
        sb.draw(doneBG,doneBound.x,doneBound.y,32,32);
        draw(sb);
    }
}
