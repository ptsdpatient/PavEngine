package com.pavengine.app.PavUI;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.hoverUIBG;
import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.StringBind;

public class DropDown extends PavWidget implements InputProcessor {
    class DropdownItem {
        Rectangle dropDownBound = new Rectangle();
        GlyphLayout dropDownLayout;
        String text;

        public DropdownItem(String text, Vector2 position) {
            this.text = text;
            dropDownLayout = new GlyphLayout(gameFont[2], text);
            dropDownBound.setPosition(position);
            dropDownBound.setSize(dropDownLayout.width, dropDownLayout.height);
        }

        public void setPosition(Vector2 position) {
            dropDownBound.setPosition(position);
        }

        public void setSize(Vector2 size) {
            dropDownBound.setSize(size.x,size.y);
        }

        public void draw(SpriteBatch sb) {
            sb.draw(uiBG[1], dropDownBound.x, dropDownBound.y, dropDownBound.width, dropDownBound.height);

            float textX = dropDownBound.x + (dropDownBound.width - dropDownLayout.width) / 2f;
            float textY = dropDownBound.y + (dropDownBound.height + dropDownLayout.height) / 2f;

            if (cursor.clicked(this.dropDownBound)) {
                sb.draw(hoverUIBG[2], dropDownBound.x, dropDownBound.y, dropDownBound.width, dropDownBound.height);
            }

            font.draw(sb, dropDownLayout, textX, textY);
        }
    }

    TextureRegion hoverTexture;
    public StringBind value;
    Array<String> list;
    Array<DropdownItem> dropdownItems = new Array<>();
    InputProcessor inputProcessor;

    public DropDown(Array<String> list,StringBind value, InputProcessor inputProcessor, BitmapFont font, TextureRegion hoverTexture, TextureRegion background) {
        this.list = list;
        this.inputProcessor = inputProcessor;
        for(String string : list) {
            dropdownItems.add(new DropdownItem(string,new Vector2(0,0)));
        }
        this.text = value.get();
        this.font = font;
        this.value = value;

        layout = new GlyphLayout(font, text);
        this.hoverTexture = hoverTexture;
        this.background = new Sprite(background);
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
        if (background != null) background.setBounds(box.x, box.y, box.width, box.height);
    }


    public void render(SpriteBatch sb) {

        if(
            !active && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && cursor.clicked(box)) {
            active = true;
            inputProcessor = Gdx.input.getInputProcessor();
            Gdx.input.setInputProcessor(this);
        }

        if(active && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && !cursor.clicked(box)) {
            for(DropdownItem item : dropdownItems) {
                if(cursor.clicked(item.dropDownBound)) {
                    value.set(item.text);
                    break;
                }
            }
            active = false;
            Gdx.input.setInputProcessor(inputProcessor);
        }

        background.draw(sb);

        float textX = box.x + (box.width - layout.width) / 2f;
        float textY = box.y + (box.height + layout.height) / 2f;

        if (isHovered && hoverTexture != null) {
            sb.draw(hoverTexture, box.x, box.y, box.width, box.height);
        }

        font.draw(sb, layout, textX, textY);

        if(active) {
            for(DropdownItem item : dropdownItems) {
                item.draw(sb);
            }
        }
    }

    @Override
    public void setSize(float spriteWidth, float spriteHeight) {
        box.setSize(Math.max(layout.width, spriteWidth), Math.max(layout.height, spriteHeight));
        for(DropdownItem item : dropdownItems) {
            item.setSize(new Vector2(spriteWidth, spriteHeight));
        }
        if (background != null) background.setSize(Math.max(layout.width, spriteWidth), Math.max(layout.height, spriteHeight));
    }

    @Override
    public void setPosition(float x, float y) {
        box.setPosition(x, y);
        int dropY = 1;
        for(DropdownItem item : dropdownItems) {
            item.setPosition(new Vector2(x, y - box.height * dropY));
            dropY += 1;
        }
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
