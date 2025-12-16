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
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.TransformTimelineWidget;
import com.pavengine.app.ObjectTransformMode;
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
            layout = new GlyphLayout(gameFont[1], value + "");
        }

        public void draw(SpriteBatch sb) {
            sb.draw(!active ? widgetBG : accentBG, bound.x, bound.y, bound.width, bound.height);
            gameFont[1].draw(sb, layout, bound.x + (bound.width - layout.width) / 2f, bound.y + (bound.height + layout.height) / 2f);
        }

        public void setY(float y) {
            bound.setY(y + bound.getY());
        }

        public void setValue(float value) {
            this.value = value;
            layout = new GlyphLayout(gameFont[1], value + "");
        }
    }

    class OptionData {

        Rectangle bound;
        String text;
        GlyphLayout layout;

        public OptionData(String text, Rectangle bound) {
            this.text = text;
            layout = new GlyphLayout(gameFont[1], text);
            this.bound = bound;
        }

        public void draw(SpriteBatch sb) {
            sb.draw(!cursor.clicked(bound) ? widgetBG : accentBG, bound.x, bound.y, bound.width, bound.height);
            gameFont[1].draw(sb, layout, bound.x + (bound.width - layout.width) / 2f, bound.y + (bound.height + layout.height) / 2f);
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
            layout = new GlyphLayout(gameFont[1], label);
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
            gameFont[1].draw(sb, layout, buttonRect.x + (buttonRect.width - layout.width) / 2f, buttonRect.y + (buttonRect.height + layout.height) / 2f);

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
        TextField[] fields = new TextField[3];

        public TransformWidgetField(String labelName, Vector2 position, Vector3 value, float yOffset) {
            this.position = position;
            this.labelLayout = new GlyphLayout(gameFont[1],labelName);
            this.position.y -= yOffset;
            this.x = new TextField(value.x, 32 * 10, new Vector2(this.position.x + labelLayout.width + 16, this.position.y));
            this.y = new TextField(value.y, 32 * 10, new Vector2(this.position.x + labelLayout.width + 16 + 32 * 10, this.position.y));
            this.z = new TextField(value.z, 32 * 10, new Vector2(this.position.x + labelLayout.width + 16 + 32 * 2 * 10, this.position.y));
            this.fontPos = new Vector2(position.x,position.y + (labelLayout.height + 45) /2);
            fields[0] = x;
            fields[1] = y;
            fields[2] = z;
        }

        public void setValue(Vector3 value) {
            x.setValue(value.x);
            y.setValue(value.y);
            z.setValue(value.z);
        }

        public void draw(SpriteBatch sb) {
                gameFont[1].draw(sb,labelLayout,fontPos.x,fontPos.y);

                x.draw(sb);
                y.draw(sb);
                z.draw(sb);
        }

        public void setY(float scrollY) {
            fontPos.y += scrollY;
            position.y += scrollY;
            for(TextField field : fields) {
                field.setY(scrollY);
            }
        }
    }

    public class TransformWidget {

        Vector2 position;
        TransformTransition data;
        TransformWidgetField posField, dirField;
        Sprite inheritDataButton;
        Array<TextField> fields = new Array<>();

        public TransformWidget(TransformTransition data, Vector2 position) {
            this.position = position;
            this.data = data;

            posField = new TransformWidgetField("Pos", new Vector2(position), data.position,0);
            dirField = new TransformWidgetField("Dir", new Vector2(position), data.position,54);

            fields.addAll(posField.fields);
            fields.addAll(dirField.fields);

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

        public void setY(float scrollY) {
            this.position.y += scrollY;
            this.inheritDataButton.translateY(scrollY);
            posField.setY(scrollY);
            dirField.setY(scrollY);
        }

    }

    public class TransformData {

        String model;
        TransformWidget initialTransform, finalTransform;
        TextField delayField;
        Button deleteButton;
        DropdownData modelData,transformModeData;
        float yPos;

        public TransformData(String model, TransformTransition initialTransform, TransformTransition finalTransform, float delay, TextureRegion buttonTexture, float yPos) {
            this.model = model;
            this.yPos = yPos;
            this.initialTransform = new TransformWidget(initialTransform, new Vector2(100,yPos - 64));
            this.finalTransform = new TransformWidget(finalTransform, new Vector2(100,yPos - 64 - 54 * 2));
            this.modelData = new DropdownData("Models", modelList, new Vector2(30, yPos));
            this.transformModeData = new DropdownData("Mode",transformModeList, new Vector2(675,yPos));
            this.delayField = new TextField(delay,32 * 7f,new Vector2(420, yPos));
            this.deleteButton = new Button(buttonTexture, new Vector2(1100, yPos));
        }

        public void draw(SpriteBatch sb) {
            if(this.initialTransform.position.y < resolution.y - 425) this.initialTransform.draw(sb);
            if(this.finalTransform.position.y < resolution.y - 425) this.finalTransform.draw(sb);
            if(this.modelData.buttonRect.y < resolution.y - 425) this.modelData.draw(sb);
            if(this.transformModeData.buttonRect.y < resolution.y - 425) this.transformModeData.draw(sb);
            if(this.delayField.bound.y < resolution.y - 425) this.delayField.draw(sb);
            if(this.deleteButton.bound.y < resolution.y - 425) this.deleteButton.draw(sb);
        }

        public void setPosition(float scrollY) {
            this.yPos+=scrollY;
            this.modelData.setY(scrollY);
            this.transformModeData.setY(scrollY);
            this.delayField.setY(scrollY);
            this.deleteButton.setY(scrollY);
            this.initialTransform.setY(scrollY);
            this.finalTransform.setY(scrollY);
        }
    }

    TransformTimelineWidget widget;
    Rectangle[] debugRect = new Rectangle[]{};
    TransformWidget initialTransform, finalTransform;
    Array<String> modelList = new Array<>(), transformModeList = new Array<>(
        new String[]{
            ObjectTransformMode.SMOOTHSTEP.name(),
            ObjectTransformMode.EASE_IN.name(),
            ObjectTransformMode.ELASTIC.name(),
            ObjectTransformMode.LINEAR.name(),
            ObjectTransformMode.STEP.name(),
        });
    DropdownData modelData, transformModeData;
    TextField delayField;
    Button createButton = new Button(
        uiControl[4],
        new Vector2(1100, resolution.y - 200 + 64)
    );

    Array<TransformData> transformDataList = new Array<>();

    public TransformCinematicModal(TransformTimelineWidget widget) {
        this.widget = widget;

        for (GameObject obj : staticObjects) {
            modelList.add(obj.name);
        }



        this.initialTransform = new TransformWidget(new TransformTransition(), new Vector2(100,resolution.y - 200));
        this.finalTransform = new TransformWidget(new TransformTransition(), new Vector2(100,resolution.y - 200 - 54 * 2));

        modelData = new DropdownData("Models", modelList, new Vector2(30, resolution.y - 200 + 64));
        transformModeData = new DropdownData("Mode",transformModeList,new Vector2(675,resolution.y - 200 + 64));

//      animationData = new DropdownData("Animation", animationList, new Vector2(430, resolution.y - 200));

        delayField = new TextField(0, 32 * 7f, new Vector2(420, resolution.y - 200 + 64));

//        for(TimelineTransformData data : this.widget.transformDataList) {
//            animationDataList.add(new TransformData(data.model,data.initialTransform, data.finalTransform, data.delay, uiControl[5],resolution.y - 256 - animationDataList.size * 56f));
//        }

    }


    @Override
    public void save() {
        this.widget.transformDataList.clear();
        for(TransformData data : transformDataList) {
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
        transformModeData.draw(sb);
        modelData.draw(sb);
        delayField.draw(sb);
        createButton.draw(sb);

        for(TransformData data : transformDataList) {
//            if(!(data.modelData.active || data.modelData.active || data.delayField.active)) if(data.yPos < resolution.y - 200 - 48)
                data.draw(sb);
        }

        for(TransformData data : transformDataList) {
//            if(data.modelData.active || data.modelData.active || data.delayField.active) if(data.yPos < resolution.y - 200 - 48)
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


    public boolean fieldWrite(TextField field, char c, String suffix) {

        if (c == '\b') {
            if (!field.input.isEmpty()) {
                field.input = field.input.substring(0, field.input.length() - 1);
            }
        } else if (c >= 32 && c != 127) {

            if (Character.isDigit(c)) {
                field.input += c;
            } else if (c == '.' && !field.input.contains(".")) {
                field.input += c;
            } else if (c == '-' && field.input.isEmpty()) {
                field.input += c;
            } else return true;
        }

        if (!field.input.equals("") &&
            !field.input.equals("-") &&
            !field.input.equals(".") &&
            !field.input.equals("-.")) {

            try {
                field.value = Float.parseFloat(field.input);
                if (field.value > widget.duration) {
                    field.value = widget.duration;
                    field.input = Float.toString(field.value);
                }
            } catch (Exception ignored) {
            }
        }

        if (field.input.isEmpty()) {
            field.value = 0f;
        }

        field.layout.setText(
            gameFont[1],
            field.input + suffix,
            Color.WHITE,
            field.bound.width,
            Align.left,
            true
        );

        return true;
    }

    @Override
    public boolean keyTyped(char c) {

        for(TransformData data : transformDataList) {
            if (data.delayField.active) {
                fieldWrite(data.delayField, c, "s");
            }
        }

        for(TextField field : initialTransform.fields) {
            if (field.active) {
                fieldWrite(field, c, "");
            }
        }

        for(TextField field : finalTransform.fields) {
            if (field.active) {
                fieldWrite(field, c, "");
            }
        }

        if (delayField.active) {
            fieldWrite(delayField, c, "s");
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

        for(TransformData data : transformDataList) {
            if (data.transformModeData.active) {
                for (OptionData optionData : data.transformModeData.optionList) {
                    if (cursor.clicked(optionData.bound)) {
                        data.transformModeData.value = optionData.text;
                        data.transformModeData.layout.setText(gameFont[1], optionData.text);
                        data.transformModeData.active = false;
                        return true;
                    }
                }
            }

            if(cursor.clicked(data.initialTransform.inheritDataButton.getBoundingRectangle())) {
                data.initialTransform.inheritData(new TransformTransition(modelData.value));
                return true;
            }

            if(cursor.clicked(data.finalTransform.inheritDataButton.getBoundingRectangle())) {
                data.finalTransform.inheritData(new TransformTransition(modelData.value));
                return true;
            }

            for(TextField field : data.initialTransform.fields) {
                field.active = false;
            }

            for(TextField field : data.finalTransform.fields) {
                field.active = false;
            }

            for(TextField field : data.initialTransform.fields) {
                if(cursor.clicked(field.bound)){
                    field.active = true;
                    return true;
                }
            }

            for(TextField field : data.finalTransform.fields) {
                if(cursor.clicked(field.bound)){
                    field.active = true;
                    return true;
                }
            }



            if (data.modelData.active) {
                for (OptionData optionData : data.modelData.optionList) {
                    if (cursor.clicked(optionData.bound)) {
                        data.modelData.value = optionData.text;
                        data.modelData.layout.setText(gameFont[1], optionData.text);
                        break;
                    }
                }
            }

            data.modelData.active = false;
            data.transformModeData.active = false;
            data.delayField.active = false;

            if (cursor.clicked(data.modelData.buttonRect)) {
                data.modelData.active = true;
                return false;
            }

            if (cursor.clicked(data.transformModeData.buttonRect)) {
                data.transformModeData.active = true;
                return false;
            }

            if (cursor.clicked(data.delayField.bound)) {
                data.delayField.active = true;
                data.delayField.layout.setText(gameFont[1], data.delayField.value + "s");
                return false;
            }

            if (cursor.clicked(data.deleteButton.bound)) {
                transformDataList.removeValue(data,true);
                return false;
            }

        }


        if (transformModeData.active) {
            for (OptionData optionData : transformModeData.optionList) {
                if (cursor.clicked(optionData.bound)) {
                    transformModeData.value = optionData.text;
                    transformModeData.layout.setText(gameFont[1], optionData.text);
                    transformModeData.active = false;
                    return true;
                }
            }
        }

        if(cursor.clicked(initialTransform.inheritDataButton.getBoundingRectangle())) {
            initialTransform.inheritData(new TransformTransition(modelData.value));
            return true;
        }

        if(cursor.clicked(finalTransform.inheritDataButton.getBoundingRectangle())) {
            finalTransform.inheritData(new TransformTransition(modelData.value));
            return true;
        }

        for(TextField field : initialTransform.fields) {
            field.active = false;
        }

        for(TextField field : finalTransform.fields) {
            field.active = false;
        }

        for(TextField field : initialTransform.fields) {
            if(cursor.clicked(field.bound)){
                field.active = true;
                return true;
            }
        }

        for(TextField field : finalTransform.fields) {
            if(cursor.clicked(field.bound)){
                field.active = true;
                return true;
            }
        }



        if (modelData.active) {
            for (OptionData optionData : modelData.optionList) {
                if (cursor.clicked(optionData.bound)) {
                    modelData.value = optionData.text;
                    modelData.layout.setText(gameFont[1], optionData.text);
                    break;
                }
            }
        }

        modelData.active = false;
        transformModeData.active = false;
        delayField.active = false;

        if (cursor.clicked(modelData.buttonRect)) {
            modelData.active = true;
            return false;
        }

        if (cursor.clicked(transformModeData.buttonRect)) {
            transformModeData.active = true;
            return false;
        }

        if (cursor.clicked(delayField.bound)) {
            delayField.active = true;
            delayField.layout.setText(gameFont[1], delayField.value + "s");
            return false;
        }

        if (cursor.clicked(createButton.bound)) {
            transformDataList.add(new TransformData(modelData.value,initialTransform.data,finalTransform.data,delayField.value,uiControl[5],resolution.y - 200 + 64 - (transformDataList.size + 1) * 300));
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

//        print((transformDataList.first().yPos + 300 + amountY * 10)  + " : " + (resolution.y - 200 + 64));

        if(!transformDataList.isEmpty()) {

           if(
               transformDataList.peek().yPos + 300 + amountY * 10 <= resolution.y - 200 + 64
               &&
                   transformDataList.first().yPos + 300 + amountY * 10 >= resolution.y - 200 + 64
           )
               for(TransformData data : transformDataList) {

                   data.setPosition(amountY*10);
               }
       }

    return false;
    }
}
