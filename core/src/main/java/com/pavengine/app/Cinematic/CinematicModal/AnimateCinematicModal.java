package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.resolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.AnimateTimelineWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.SubtitleTimelineWidget;

public class AnimateCinematicModal extends CinematicModal {

    AnimateTimelineWidget widget;
    GlyphLayout textLayout;
    String text;
    Color color;
    float blinkTimer = 0f;
    boolean textAreaActive = false,cursorBlink = false;

    public Rectangle textArea = new Rectangle(resolution.x/2f-(resolution.x*0.6f)/2f,400, resolution.x*0.6f,100);
    public Rectangle colorArea = new Rectangle(resolution.x/2f-(resolution.x*0.4f)/2f,255, resolution.x*0.4f,100);
    Rectangle[] debugRect = new Rectangle[]{ textArea, colorArea };

    public AnimateCinematicModal(AnimateTimelineWidget widget) {
        this.widget = widget;

        textLayout = new GlyphLayout(gameFont[2], text  + "_",color, textArea.width, Align.left,true);

    }

    @Override
    public void save() {
    }

    @Override
    public Rectangle[] getDebugRect() {
        return debugRect;
    }

    @Override
    public void draw(SpriteBatch sb) {
        blinkTimer += Gdx.graphics.getDeltaTime();

        for(Rectangle rect : debugRect) {
            sb.draw(widgetBG,rect.x,rect.y,rect.width,rect.height);
        }

        if(blinkTimer > 0.4) {
            cursorBlink = !cursorBlink;
            blinkTimer = 0;
        }

        if(textAreaActive && cursorBlink) {
//            sb.draw(accentBG,textArea.x + textLayout.width + 3,textArea.y + 8,16,5);
        }


        sb.flush();
        Rectangle scissor = new Rectangle();
        ScissorStack.calculateScissors(overlayCamera, sb.getTransformMatrix(), textArea, scissor);
        ScissorStack.pushScissors(scissor);

        gameFont[2].draw(sb, textLayout, textArea.x + 16, textArea.y + 16 + textLayout.height);

        sb.flush();
        ScissorStack.popScissors();


        if(cursor.clicked(textArea) && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            textAreaActive= !textAreaActive;
        }
    }



    @Override
    public boolean keyUp(int keycode) {
        return false;
    }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (!textAreaActive) return false;

        if (character == '\b') {
            if (!text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
            }
        } else if (character >= 32 && character != 127) {
            text = text + character;
        }

        textLayout.setText(gameFont[2], text + "_" ,color, textArea.width, Align.left,true);
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
