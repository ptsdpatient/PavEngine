package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiControl;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.model.Animation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.AnimateTimelineWidget;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.StringBind;

import java.util.Objects;

public class AnimateCinematicModal extends CinematicModal {

    class Button {
        Rectangle bound = new Rectangle(0,0,48,48);
        TextureRegion region;

        public Button(TextureRegion region, Vector2 position) {
            this.region = region;
            bound.setPosition(position);
        }

        public void draw(SpriteBatch sb) {
            sb.draw(region,bound.x,bound.y,bound.width,bound.height);
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
    }

    public class DropdownData {

        public String value;
        Array<OptionData> optionList = new Array<>();
        GlyphLayout layout;
        boolean active = false;
        Rectangle buttonRect = new Rectangle(0, 0, 364, 48);

        String label;

        public DropdownData(String label, Array<String> list, Vector2 position) {

            this.label = label;
            this.value = label;
            buttonRect.setPosition(position);
            layout = new GlyphLayout(gameFont[2], label);
            float yOffset = 0;
            for (String string : list) {
                yOffset += 56;
                optionList.add(new OptionData(string, new Rectangle(position.x, position.y - yOffset, 364, 48)));
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
                    option.draw(sb);
                }
            }
        }
    }

    public class AnimationData {
        Array<String> animationList = new Array<>();
        DropdownData modelData, animationData;
        TextField delayField;
        Button deleteButton;

        public AnimationData(String model, String animation, float delay, TextureRegion buttonTexture, float yPos) {
            modelData = new DropdownData(model, modelList, new Vector2(50, yPos));
            animationData = new DropdownData(animation, animationList, new Vector2(430, yPos));
            delayField = new TextField(delay,new Vector2(820, yPos));
            deleteButton = new Button(buttonTexture, new Vector2(1100, yPos));
        }

        public void draw(SpriteBatch sb) {
            modelData.draw(sb);
            animationData.draw(sb);
            delayField.draw(sb);
            deleteButton.draw(sb);
        }
    }

    AnimateTimelineWidget widget;
    Rectangle[] debugRect = new Rectangle[]{};
    DropdownData modelData, animationData;
    Array<String> modelList = new Array<>(), animationList = new Array<>();
    TextField delayField;
    Button createButton = new Button(uiControl[4],new Vector2(1100, resolution.y - 200));
    Array<AnimationData> animationDataList = new Array<>();

    public AnimateCinematicModal(AnimateTimelineWidget widget) {
        this.widget = widget;

        for (GameObject obj : staticObjects) {
            modelList.add(obj.name);
        }

        modelData = new DropdownData("Models", modelList, new Vector2(50, resolution.y - 200));
        animationData = new DropdownData("Animation", animationList, new Vector2(430, resolution.y - 200));

        delayField = new TextField(0, new Vector2(820, resolution.y - 200));

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

        modelData.draw(sb);
        animationData.draw(sb);
        delayField.draw(sb);
        createButton.draw(sb);

        for(AnimationData data : animationDataList) {
            data.draw(sb);
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
            if(cursor.clicked(deleteBound)) {
                this.widget.delete();
            }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {



        for(AnimationData data : animationDataList) {
            if (data.modelData.active) {
                for (OptionData optionData : data.modelData.optionList) {
                    if (cursor.clicked(optionData.bound)) {
                        data.animationList.clear();
                        if (staticObjects.size < 1) break;
                        GameObject selectedModel = staticObjects.peek();
                        for (GameObject obj : staticObjects) {
                            if (Objects.equals(optionData.text, obj.name)) {
                                selectedModel = obj;
                                break;
                            }
                        }
                        for (Animation animation : selectedModel.scene.modelInstance.animations) {
                            data.animationList.add(animation.id);
                        }
                        data.animationData.set(data.animationList);
                        data.modelData.value = optionData.text;
                        data.modelData.layout.setText(gameFont[2], optionData.text);
                        break;
                    }
                }
            }

            if (data.animationData.active) {
                for (OptionData optionData : data.animationData.optionList) {
                    if (cursor.clicked(optionData.bound)) {
                        data.animationData.value = optionData.text;
                        data.animationData.layout.setText(gameFont[2], optionData.text);
                        break;
                    }
                }
            }

            data.modelData.active = false;
            data.animationData.active = false;
            data.delayField.active = false;

            if (cursor.clicked(data.modelData.buttonRect)) {
                data.modelData.active = true;
                print("active!");
                return false;
            }

            if (cursor.clicked(data.animationData.buttonRect)) {
                data.animationData.active = true;
                print("active!");
                return false;
            }

            if (cursor.clicked(data.delayField.bound)) {
                data.delayField.active = true;
                data.delayField.layout.setText(gameFont[2], data.delayField.value + "s");
                return false;
            }

            if(cursor.clicked(data.deleteButton.bound)) {
                animationDataList.removeValue(data,true);
            }
        }

        if(cursor.clicked(createButton.bound)) {
            animationDataList.add(new AnimationData(modelData.value,animationData.value,delayField.value,uiControl[5],resolution.y - 256 - animationDataList.size * 56f));
            return true;
        }

        if (modelData.active) {
            for (OptionData optionData : modelData.optionList) {
                if (cursor.clicked(optionData.bound)) {
                    animationList.clear();
                    if (staticObjects.size < 1) break;
                    GameObject selectedModel = staticObjects.peek();
                    for (GameObject obj : staticObjects) {
                        if (Objects.equals(optionData.text, obj.name)) {
                            selectedModel = obj;
                            break;
                        }
                    }
                    for (Animation animation : selectedModel.scene.modelInstance.animations) {
                        animationList.add(animation.id);
                    }
                    animationData.set(animationList);
                    modelData.value = optionData.text;
                    modelData.layout.setText(gameFont[2], optionData.text);
                    break;
                }
            }
        }

        if (animationData.active) {
            for (OptionData optionData : animationData.optionList) {
                if (cursor.clicked(optionData.bound)) {
                    animationData.value = optionData.text;
                    animationData.layout.setText(gameFont[2], optionData.text);
                }
            }
        }

        modelData.active = false;
        animationData.active = false;
        delayField.active = false;

        if (cursor.clicked(modelData.buttonRect)) {
            modelData.active = true;
            return false;
        }

        if (cursor.clicked(animationData.buttonRect)) {
            animationData.active = true;
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
        return false;
    }
}
