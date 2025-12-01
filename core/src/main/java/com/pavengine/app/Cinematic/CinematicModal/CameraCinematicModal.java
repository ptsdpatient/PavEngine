package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.CameraTimelineWidget;

public class CameraCinematicModal extends CinematicModal {

    static class TextField {
        boolean hovered = false;
        String label;
        String value;
        GlyphLayout layout;
        public Rectangle bound;

        public TextField(String label,Vector2 position) {
            this.label = label;
            value = "0.0";
            layout = new GlyphLayout(gameFont[2],"0.0");
            bound = new Rectangle(position.x,position.y - 26,32*4,32);
        }

        public void draw(SpriteBatch sb) {
            if(hovered)
                sb.draw(accentBG,bound.x ,bound.y ,bound.width,bound.height);
            gameFont[2].draw(sb,layout,bound.x + 6,bound.y + 26);
        }

        public void set(String value) {
            this.value = value;
            layout = new GlyphLayout(gameFont[2],this.value + "_");
        }
    }

    public static class Property {
        String[] fieldLabel = new String[] {"X","Y","Z"};
        TextField[] fields = new TextField[3];
        String label;
        Vector2 position;
        public Property(String label, Vector2 position) {
            this.label = label;
            this.position = position;
            for(int i = 0; i < 3; i++) {
                fields[i] = new TextField(fieldLabel[i], new Vector2(position.x + 64 + 64 * 2 * i, position.y));
            }
        }
        public void draw(SpriteBatch sb) {
            gameFont[2].draw(sb,label,position.x,position.y);
            for(TextField field : fields) {
                field.draw(sb);
            }
        }

    }

    CameraTimelineWidget widget;
    TextField selectedTextField;
    GlyphLayout textLayout;
    String text;
    Color color;
    float blinkTimer = 0f;
    boolean textAreaActive = false,cursorBlink = false;
    public Rectangle startInfo = new Rectangle(resolution.x/2f-(resolution.x*0.365f)/2f,resolution.y - 175, resolution.x*0.365f,80);
    public Rectangle endInfo = new Rectangle(resolution.x/2f-(resolution.x*0.365f)/2f,200, resolution.x*0.365f,80);
    public Property startInfoPos = new Property("Pos", new Vector2(startInfo.x + 16, startInfo.y - 16 + 80));
    Rectangle[] debugRect = new Rectangle[]{startInfo, endInfo };

    public CameraCinematicModal(CameraTimelineWidget widget) {
        this.widget = widget;
        this.text = widget.text;
        this.color = widget.color;
        textLayout = new GlyphLayout(gameFont[2], text  + "_",color, startInfo.width, Align.left,true);
    }

    @Override
    public void save() {
        widget.color = this.color;
        widget.text = this.text;

        print(widget.text);
    }

    @Override
    public Rectangle[] getDebugRect() {
        return debugRect;
    }

    @Override
    public void debug(SpriteBatch sb) {
        for(TextField field : startInfoPos.fields) {
            debugRectangle(field.bound,Color.YELLOW);
        }
    }

    @Override
    public void draw(SpriteBatch sb) {
        blinkTimer += Gdx.graphics.getDeltaTime();

        for(Rectangle rect : debugRect) {
            sb.draw(widgetBG,rect.x,rect.y,rect.width,rect.height);
        }

        startInfoPos.draw(sb);

        if(blinkTimer > 0.4) {
            cursorBlink = !cursorBlink;
            blinkTimer = 0;
        }

        if(textAreaActive && cursorBlink) {
//            sb.draw(accentBG,textArea.x + textLayout.width + 3,textArea.y + 8,16,5);
        }


        for (TextField field : startInfoPos.fields) {
            field.hovered = cursor.clicked(field.bound);
            if(
                field.hovered
                && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
            ) {
                selectedTextField = field;
                selectedTextField.set(field.value);
            }
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

        textLayout.setText(gameFont[2], text + "_" ,color, startInfo.width, Align.left,true);
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
