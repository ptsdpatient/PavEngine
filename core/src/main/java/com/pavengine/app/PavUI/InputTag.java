package com.pavengine.app.PavUI;

import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pavengine.app.AnimationCurve;
import com.pavengine.app.StringBind;

import javafx.beans.binding.StringBinding;

public class InputTag extends PavWidget implements InputProcessor {
    GlyphLayout layout;
    TextureRegion hoverTexture;
    public boolean active = false;
    InputProcessor inputProcessor;
    public StringBind binding;


    public InputTag( StringBind binding, BitmapFont font, TextureRegion textureRegion,TextureRegion hoverTexture, ClickBehavior clickBehavior) {
        this.binding = binding;
        this.text = binding.get();
        this.font = font;
        this.clickBehavior = clickBehavior;
        layout = new GlyphLayout(gameFont[2], text, Color.WHITE, 224, Align.left,true);
        this.background = new Sprite(textureRegion);
        this.hoverTexture = new TextureRegion(hoverTexture);
        this.box = new Rectangle(0, 0, layout.width, layout.height);
        type = WidgetType.TextButton;
    }

    @Override
    public Vector2 getCenter() {
        return new Vector2(box.x + box.width / 2f, box.y + box.height / 2f);
    }

    @Override
    public boolean clicked(Vector2 point) {
        return box.contains(point);
    }

    @Override
    public void update() {
        if (background != null)
            background.setBounds(box.x, box.y, box.width, box.height);
    }


    @Override
    public void render(SpriteBatch sb) {

        if (background != null)
            background.draw(sb);

        if(
            !active && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && cursor.clicked(box)) {
            active = true;
            inputProcessor = Gdx.input.getInputProcessor();
            layout.setText(gameFont[2], text + "_" , Color.WHITE, box.width, Align.left,true);
            Gdx.input.setInputProcessor(this);
        }

        if(active && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !cursor.clicked(box)) {
            Gdx.input.setInputProcessor(inputProcessor);
            layout.setText(gameFont[2], text, Color.WHITE, box.width, Align.left,true);
            active = false;
        }


        float textX = box.x + (box.width - layout.width) / 2f;
        float textY = box.y + (box.height + layout.height) / 2f;

        if (isHovered && hoverTexture != null) {
            sb.draw(hoverTexture, box.x, box.y, box.width, box.height);
        }

        font.draw(sb, layout, textX, textY);

    }

    @Override
    public void setSize(float spriteWidth, float spriteHeight) {
        box.setSize(Math.max(layout.width, spriteWidth), Math.max(layout.height, spriteHeight));
        if (background != null) background.setSize(Math.max(layout.width, spriteWidth), Math.max(layout.height, spriteHeight));
    }

    @Override
    public void setPosition(float x, float y) {
        box.setPosition(x, y);
        if (background != null) background.setPosition(x, y);
    }

    @Override
    public boolean keyDown(int keycode) {

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        if (!active) return false;

        if (character == '\b') {
            if (!text.isEmpty()) {
                text = text.substring(0, text.length() - 1);
            }
        } else if (character >= 32 && character != 127) {
            text = text + character;
        }

        binding.set(text);

        layout.setText(gameFont[2], text + "_" , Color.WHITE, box.width, Align.left,true);

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
