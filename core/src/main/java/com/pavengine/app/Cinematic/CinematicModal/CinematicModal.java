package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

public abstract class CinematicModal implements InputProcessor {
    TextureRegion bg,closeBG;
    public Rectangle closeBound = new Rectangle();

    public CinematicModal(TextureRegion bg, TextureRegion closeBG) {
        this.bg = bg;
        this.closeBG = closeBG;
        closeBound.set(resolution.x-60,resolution.y-60,32,32);
        Gdx.input.setInputProcessor(this);
    }

    public abstract void draw(SpriteBatch sb);

    public void render(SpriteBatch sb) {
        sb.draw(bg,0,0,resolution.x,resolution.y);
        sb.draw(closeBG,closeBound.x,closeBound.y,32,32);
    }
}
