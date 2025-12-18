package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavEngine.uiControl;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.SoundTimelineWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.TimelineSoundData;
import com.pavengine.app.PavSound.Sounds;

public class SoundCinematicModal extends CinematicModal {

    class Button {
        Rectangle bound = new Rectangle(0, 0, 48, 48);
        TextureRegion region;

        public Button(TextureRegion region, Vector2 position) {
            this.region = region;
            bound.setPosition(position);
        }

        public void draw(SpriteBatch sb) {
            sb.draw(region, bound.x, bound.y, bound.width, bound.height);
        }

        public void setY(float y) {
            bound.setY(y + bound.getY());
        }
    }

    class TextField {
        String input = "";
        Rectangle bound = new Rectangle(0, 0, 32 * 7, 48);
        GlyphLayout layout;
        float value = 0;
        boolean active = false;

        TextField(float value, Vector2 position) {
            this.value = value;
            bound.setPosition(position);
            layout = new GlyphLayout(gameFont[2], value + "s");
        }

        public void draw(SpriteBatch sb) {
            sb.draw(!cursor.clicked(bound) ? widgetBG : accentBG, bound.x, bound.y, bound.width, bound.height);
            gameFont[2].draw(sb, layout, bound.x + (bound.width - layout.width) / 2f, bound.y + (bound.height + layout.height) / 2f);
        }

        public void setY(float y) {
            bound.setY(y + bound.getY());
        }
    }

    class OptionData {

        Rectangle bound;
        String text;
        GlyphLayout layout;

        public OptionData(String text, Rectangle bound) {
            this.text = text;
            layout = new GlyphLayout(gameFont[2], text);
            this.bound = bound;
        }

        public void draw(SpriteBatch sb) {
            sb.draw(!cursor.clicked(bound) ? widgetBG : accentBG, bound.x, bound.y, bound.width, bound.height);
            gameFont[2].draw(sb, layout, bound.x + (bound.width - layout.width) / 2f, bound.y + (bound.height + layout.height) / 2f);
        }

        public void setY(float y) {
            bound.setY(y + bound.getY());
        }
    }

    public class DropdownData {

        public String value;
        Array<OptionData> optionList = new Array<>();
        GlyphLayout layout;
        boolean active = false;
        Rectangle buttonRect = new Rectangle(0, 0, 600, 48);

        String label;

        public DropdownData(String label, Array<String> list, Vector2 position) {

            this.label = label;
            this.value = label;
            buttonRect.setPosition(position);
            layout = new GlyphLayout(gameFont[2], label);
            float yOffset = 0;
            for (String string : list) {
                yOffset += 56;
                optionList.add(new OptionData(string, new Rectangle(position.x, position.y - yOffset, 600, 48)));
            }
        }

        public void set(Array<String> list) {
            float yOffset = 0;
            for (String string : list) {
                yOffset += 56;
                optionList.add(new OptionData(string, new Rectangle(buttonRect.x, buttonRect.y - yOffset, 364, 48)));
            }
        }

        public void draw(SpriteBatch sb) {

            sb.draw(!cursor.clicked(buttonRect) ? widgetBG : accentBG, buttonRect.x, buttonRect.y, buttonRect.width, buttonRect.height);
            gameFont[2].draw(sb, layout, buttonRect.x + (buttonRect.width - layout.width) / 2f, buttonRect.y + (buttonRect.height + layout.height) / 2f);

            if (active) {
                for (OptionData option : optionList) {
                    if (option.bound.y >= buttonRect.y - 400 && option.bound.y < buttonRect.y - 45) {
                        option.draw(sb);
                    }
                }
            }
        }

        public void setY(float y) {
            buttonRect.setY(y + buttonRect.getY());
        }

        public void scrollOptions(float y) {
            for (OptionData option : optionList) {
                option.setY(y);
            }
        }
    }

    public class SoundData {
        DropdownData soundData;
        TextField delayField;
        Button deleteButton;
        float yPos;

        public SoundData(String sound, float delay, TextureRegion buttonTexture, float yPos) {
            this.yPos = yPos;
            soundData = new DropdownData(sound, soundsList, new Vector2(120, yPos));
            delayField = new TextField(delay, new Vector2(760, yPos));
            deleteButton = new Button(buttonTexture, new Vector2(1000, yPos));
        }

        public void draw(SpriteBatch sb) {
            soundData.draw(sb);
            delayField.draw(sb);
            deleteButton.draw(sb);
        }

        public void setPosition(float scrollY) {
            yPos += scrollY;
            soundData.setY(scrollY);
            delayField.setY(scrollY);
            deleteButton.setY(scrollY);
        }
    }

    SoundTimelineWidget widget;
    Rectangle[] debugRect = new Rectangle[]{};
    DropdownData soundData;
    Array<String> soundsList = new Array<>();
    TextField delayField;
    Button createButton = new Button(uiControl[4], new Vector2(1000, resolution.y - 200));
    Array<SoundData> animationDataList = new Array<>();

    public SoundCinematicModal(SoundTimelineWidget widget) {
        this.widget = widget;

        for (Sounds sound : soundBox.sounds) {
            this.soundsList.add(sound.name);
        }

        soundData = new DropdownData("Sound", soundsList, new Vector2(120, resolution.y - 200));

        delayField = new TextField(0, new Vector2(760, resolution.y - 200));

        for (TimelineSoundData data : this.widget.soundDataList) {
            animationDataList.add(new SoundData(data.sound, data.delay,uiControl[5], resolution.y - 256 - animationDataList.size * 56f));
        }
    }


    @Override
    public void save() {
        this.widget.soundDataList.clear();
        for (SoundData data : animationDataList) {
            this.widget.soundDataList.add(new TimelineSoundData(data.soundData.value, data.delayField.value));
        }
    }

    @Override
    public Rectangle[] getDebugRect() {
        return debugRect;
    }

    @Override
    public void draw(SpriteBatch sb) {

        soundData.draw(sb);
        delayField.draw(sb);
        createButton.draw(sb);

        for (SoundData data : animationDataList) {
            if (data.yPos < soundData.buttonRect.y - 48) {
                data.draw(sb);
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
    public boolean keyTyped(char c) {

        for (SoundData data : animationDataList) {
            if (data.delayField.active) {
                if (c == '\b') {
                    if (!data.delayField.input.isEmpty()) {
                        data.delayField.input = data.delayField.input.substring(0, data.delayField.input.length() - 1);
                    }
                } else if (c >= 32 && c != 127) {

                    if (Character.isDigit(c)) {
                        data.delayField.input += c;
                    } else if (c == '.' && !data.delayField.input.contains(".")) {
                        data.delayField.input += c;
                    } else if (c == '-' && data.delayField.input.isEmpty()) {
                        data.delayField.input += c;
                    } else return true;
                }

                if (!data.delayField.input.equals("") &&
                    !data.delayField.input.equals("-") &&
                    !data.delayField.input.equals(".") &&
                    !data.delayField.input.equals("-.")) {

                    try {
                        data.delayField.value = Float.parseFloat(data.delayField.input);
                        if (data.delayField.value > widget.duration) {
                            data.delayField.value = widget.duration;
                            data.delayField.input = Float.toString(data.delayField.value);
                        }
                    } catch (Exception ignored) {
                    }
                }

                if (data.delayField.input.isEmpty()) {
                    data.delayField.value = 0f;
                }

                data.delayField.layout.setText(
                    gameFont[2],
                    data.delayField.input + "s",
                    Color.WHITE,
                    data.delayField.bound.width,
                    Align.left,
                    true
                );
            }
        }

        if (delayField.active) {
            if (c == '\b') {
                if (!delayField.input.isEmpty()) {
                    delayField.input = delayField.input.substring(0, delayField.input.length() - 1);
                }
            } else if (c >= 32 && c != 127) {

                if (Character.isDigit(c)) {
                    delayField.input += c;
                } else if (c == '.' && !delayField.input.contains(".")) {
                    delayField.input += c;
                } else if (c == '-' && delayField.input.isEmpty()) {
                    delayField.input += c;
                } else return true;
            }

            if (!delayField.input.equals("") &&
                !delayField.input.equals("-") &&
                !delayField.input.equals(".") &&
                !delayField.input.equals("-.")) {

                try {
                    delayField.value = Float.parseFloat(delayField.input);
                    if (delayField.value > widget.duration) {
                        delayField.value = widget.duration;
                        delayField.input = Float.toString(delayField.value);
                    }
                } catch (Exception ignored) {
                }
            }

            if (delayField.input.isEmpty()) {
                delayField.value = 0f;
            }

            delayField.layout.setText(
                gameFont[2],
                delayField.input + "s",
                Color.WHITE,
                delayField.bound.width,
                Align.left,
                true
            );


        }
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (cursor.clicked(deleteBound)) {
            this.widget.delete();
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {


        for (SoundData data : animationDataList) {
            if (data.soundData.active) {
                for (OptionData optionData : data.soundData.optionList) {
                    if (cursor.clicked(optionData.bound)) {
                        data.soundData.value = optionData.text;
                        data.soundData.layout.setText(gameFont[2], optionData.text);
                        break;
                    }
                }
            }

            data.soundData.active = false;
            data.delayField.active = false;


            if (cursor.clicked(data.soundData.buttonRect)) {
                data.soundData.active = true;
                print("active!");
                return false;
            }

            if (cursor.clicked(data.delayField.bound)) {
                data.delayField.active = true;
                data.delayField.layout.setText(gameFont[2], data.delayField.value + "s");
                return false;
            }

            if (cursor.clicked(data.deleteButton.bound)) {
                animationDataList.removeValue(data, true);
            }
        }

        if (cursor.clicked(createButton.bound)) {
            animationDataList.add(new SoundData(soundData.value, delayField.value, uiControl[5], resolution.y - 256 - animationDataList.size * 56f));
            return true;
        }


        if (soundData.active) {
            for (OptionData optionData : soundData.optionList) {
                if (cursor.clicked(optionData.bound)) {
                    soundData.value = optionData.text;
                    soundData.layout.setText(gameFont[2], optionData.text);
                }
            }
        }


        soundData.active = false;
        delayField.active = false;


        if (cursor.clicked(soundData.buttonRect)) {
            soundData.active = true;
            return false;
        }

        if (cursor.clicked(delayField.bound)) {
            delayField.active = true;
            delayField.layout.setText(gameFont[2], delayField.value + "s");
            return false;
        }


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
        if (
            soundData.optionList.peek().bound.y + 54 + amountY * 10 <= soundData.buttonRect.y
                && soundData.optionList.first().bound.y + 60 + amountY * 10 >= soundData.buttonRect.y
        ) {
            soundData.scrollOptions(amountY * 10);
        }

        if (!animationDataList.isEmpty()) {


            if (
                animationDataList.peek().yPos + 54 + amountY * 10 <= resolution.y - 200
                    && animationDataList.first().yPos + 60 + amountY * 10 >= resolution.y - 200
            ) {
                for (SoundData data : animationDataList) {
                    data.setPosition(amountY * 10);
                }
            }
        }


        return false;
    }
}
