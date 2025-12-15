package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.icons;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiControl;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.Range.range;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.TimelineTransformData;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.TransformTimelineWidget;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.TransformTransition;

public class TransformCinematicModal extends CinematicModal {

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

        TextField(float value, float width, Vector2 position) {
            this.value = value;
            bound.setPosition(position);
            bound.setWidth(width);
            layout = new GlyphLayout(gameFont[2], value + "");
        }

        public void draw(SpriteBatch sb) {
            sb.draw(!cursor.clicked(bound) ? widgetBG : accentBG, bound.x, bound.y, bound.width, bound.height);
            gameFont[2].draw(sb, layout, bound.x + (bound.width - layout.width) / 2f, bound.y + (bound.height + layout.height) / 2f);
        }

        public void setY(float y) {
            bound.setY(y + bound.getY());
        }

        public void setValue(float value) {
            this.value = value;
            layout = new GlyphLayout(gameFont[2], value + "");
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
                    if(option.bound.y < buttonRect.y - 48 && option.bound.y > buttonRect.y - 356)
                        option.draw(sb);
                }
            }
        }

        public void setY(float y) {
            buttonRect.setY(y + buttonRect.getY());
        }

        public void scrollOption(float v) {
            for(OptionData optionData : optionList) {
                optionData.setY(v);
            }
        }
    }

    public class TransformWidgetField {
        Vector2 position;
        TextField x,y,z;
        GlyphLayout labelLayout;
        Vector2 fontPos;

        public TransformWidgetField(String labelName, Vector2 position, Vector3 value, float yOffset) {
            this.position = position;
            this.labelLayout = new GlyphLayout(gameFont[2],labelName);
            this.position.y -= yOffset;
            this.x = new TextField(value.x, 32 * 10, new Vector2(this.position.x + labelLayout.width + 16, this.position.y));
            this.y = new TextField(value.y, 32 * 10, new Vector2(this.position.x + labelLayout.width + 16 + 32 * 10, this.position.y));
            this.z = new TextField(value.z, 32 * 10, new Vector2(this.position.x + labelLayout.width + 16 + 32 * 2 * 10, this.position.y));
            this.fontPos = new Vector2(position.x,position.y + (labelLayout.height + 45) /2);
        }

        public void setValue(Vector3 value) {
            x.setValue(value.x);
            y.setValue(value.y);
            z.setValue(value.z);
        }

        public void draw(SpriteBatch sb) {
            gameFont[2].draw(sb,labelLayout,fontPos.x,fontPos.y);
            x.draw(sb);
            y.draw(sb);
            z.draw(sb);
        }
    }

    public class TransformWidget {

        Vector2 position;
        TransformTransition data;
        TransformWidgetField posField, dirField;
        Sprite inheritDataButton;

        public TransformWidget(TransformTransition data, Vector2 position) {
            this.position = position;
            this.data = data;

            posField = new TransformWidgetField("Pos", new Vector2(position), data.position,0);
            dirField = new TransformWidgetField("Dir", new Vector2(position), data.position,54);

            inheritDataButton = new Sprite(icons[1]);
            inheritDataButton.setOriginCenter();
            inheritDataButton.setSize(54,54);
            inheritDataButton.setPosition(position.x + 1070, position.y - 54/2f);
        }

        public void draw(SpriteBatch sb) {
            posField.draw(sb);
            dirField.draw(sb);
            inheritDataButton.draw(sb);
        }

        public void inheritData(TransformTransition data) {
            posField.setValue(data.position.cpy());
            dirField.setValue(data.direction.cpy());
        }

    }

    public class TransformData {
        String model = "";
        Array<String> animationList = new Array<>();
        TransformWidget initialTransform, finalTransform;
        TextField delayField;
        Button deleteButton;
        float yPos;

        public TransformData(String model, TransformTransition initialTransform, TransformTransition finalTransform, float delay, TextureRegion buttonTexture, float yPos) {
            this.model = model;
            this.yPos = yPos;
            this.initialTransform = new TransformWidget(initialTransform, new Vector2(10,resolution.y - 200));
            this.finalTransform = new TransformWidget(finalTransform, new Vector2(10,resolution.y - 200));

            delayField = new TextField(delay,32 * 7f,new Vector2(820, yPos));
            deleteButton = new Button(buttonTexture, new Vector2(1100, yPos));
        }

        public void draw(SpriteBatch sb) {
//            modelData.draw(sb);
//            animationData.draw(sb);
            initialTransform.draw(sb);
            finalTransform.draw(sb);
            delayField.draw(sb);
            deleteButton.draw(sb);
        }

        public void setPosition(float scrollY) {
            yPos+=scrollY;
//            modelData.setY(scrollY);
//            animationData.setY(scrollY);
            delayField.setY(scrollY);
            deleteButton.setY(scrollY);
        }
    }

    TransformTimelineWidget widget;
    Rectangle[] debugRect = new Rectangle[]{};
    TransformWidget initialTransform, finalTransform;
    Array<String> modelList = new Array<>();
    DropdownData modelData;
    TextField delayField;
    Button createButton = new Button(
        uiControl[4],
        new Vector2(1100, resolution.y - 200 + 64)
    );
    Array<TransformData> animationDataList = new Array<>();

    public TransformCinematicModal(TransformTimelineWidget widget) {
        this.widget = widget;

        for (GameObject obj : staticObjects) {
            modelList.add(obj.name);
        }

        this.initialTransform = new TransformWidget(new TransformTransition(), new Vector2(100,resolution.y - 200));
        this.finalTransform = new TransformWidget(new TransformTransition(), new Vector2(100,resolution.y - 200 - 54 * 2));

        modelData = new DropdownData("Models", modelList, new Vector2(30, resolution.y - 200 + 64));

//      animationData = new DropdownData("Animation", animationList, new Vector2(430, resolution.y - 200));

        delayField = new TextField(0, 32 * 7f, new Vector2(400, resolution.y - 200 + 64));

//        for(TimelineTransformData data : this.widget.transformDataList) {
//            animationDataList.add(new TransformData(data.model,data.initialTransform, data.finalTransform, data.delay, uiControl[5],resolution.y - 256 - animationDataList.size * 56f));
//        }

    }


    @Override
    public void save() {
        this.widget.transformDataList.clear();
        for(TransformData data : animationDataList) {
//            this.widget.transformDataList.add(
//                new TimelineTransformData(data.model,data.initialTransform, data.finalTransform,data.delayField.value,false)
//            );
        }
    }

    @Override
    public Rectangle[] getDebugRect() {
        return debugRect;
    }

    @Override
    public void draw(SpriteBatch sb) {
        initialTransform.draw(sb);
        finalTransform.draw(sb);

//        for(TransformData data : animationDataList) {
//            if(!(data.modelData.active || data.animationData.active || data.delayField.active)) if(data.yPos < resolution.y - 200 - 48)
//                data.draw(sb);
//        }

//        for(TransformData data : animationDataList) {
//            if(data.modelData.active || data.animationData.active || data.delayField.active) if(data.yPos < resolution.y - 200 - 48)
//                data.draw(sb);
//        }

        modelData.draw(sb);
        delayField.draw(sb);
        createButton.draw(sb);

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

        for(TransformData data : animationDataList) {
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
            if(cursor.clicked(deleteBound)) {
                this.widget.delete();
            }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {

        if(cursor.clicked(initialTransform.inheritDataButton.getBoundingRectangle())) {
            initialTransform.inheritData(new TransformTransition(modelData.value));
        }

//        if(cursor.clicked(createButton.bound)) {
//            animationDataList.add(
//                new TransformData(modelData.value,animationData.value,delayField.value,uiControl[5],resolution.y - 256 - animationDataList.size * 56f));
//            return true;
//        }

        if (modelData.active) {
            for (OptionData optionData : modelData.optionList) {
                if (cursor.clicked(optionData.bound)) {
                    modelData.value = optionData.text;
                    modelData.layout.setText(gameFont[2], optionData.text);
                    break;
                }
            }
        }


        modelData.active = false;
        delayField.active = false;

        if (cursor.clicked(modelData.buttonRect)) {
            modelData.active = true;
            return false;
        }
//
//        if (cursor.clicked(animationData.buttonRect)) {
//            animationData.active = true;
//            return false;
//        }

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

//        if(modelData.active && !modelData.optionList.isEmpty()) {
//             if(
//                modelData.optionList.peek().bound.y + 54 + amountY*10 <= modelData.buttonRect.y
//                    && modelData.optionList.first().bound.y + 60 + amountY*10 >= modelData.buttonRect.y
//            )
//            {
//                modelData.scrollOption(amountY * 10);
//                return true;
//            }
//        }
//
//        if(animationData.active && !animationData.optionList.isEmpty()) {
//             if(
//                animationData.optionList.peek().bound.y + 54 + amountY*10 <= animationData.buttonRect.y
//                    && animationData.optionList.first().bound.y + 60 + amountY*10 >= animationData.buttonRect.y
//            )
//            {
//                animationData.scrollOption(amountY * 10);
//                return true;
//            }
//
//        }

       if(!animationDataList.isEmpty()) {

           if(
               animationDataList.peek().yPos + 54 + amountY*10 <= resolution.y - 200
                   && animationDataList.first().yPos + 60 + amountY*10 >= resolution.y - 200
           )
               for(TransformData data : animationDataList) {

                   data.setPosition(amountY*10);
               }
       }

    return false;
    }
}
