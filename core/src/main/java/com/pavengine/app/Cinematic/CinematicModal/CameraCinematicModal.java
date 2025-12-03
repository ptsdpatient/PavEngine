package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.icons;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.CameraTransform;
import com.pavengine.app.CameraTransitionMode;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.CameraTimelineWidget;
import com.pavengine.app.Dropdowns.CinematicCameraModalDropdown;
import com.pavengine.app.Dropdowns.Dropdown;

public class CameraCinematicModal extends CinematicModal {

    static class TextField {
        boolean hovered = false;
        String label;
        String value;
        GlyphLayout layout;
        public Rectangle bound;

        public TextField(String label, float value, Vector2 position) {
            this.label = label;
            this.value = value == 0? "0.0" : value + "";
            layout = new GlyphLayout(gameFont[2],this.value);
            bound = new Rectangle(position.x,position.y - 26,32*8,32);
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

        public Property(String label, float[] values,Vector2 position) {
            this.label = label;
            this.position = position;
            for(int i = 0; i < 3; i++) {
                fields[i] = new TextField(fieldLabel[i], values[i], new Vector2(position.x + 32*3 + 32 * 8 * i, position.y));
            }
        }

        private float toFloatSafe(String s) {
            if (s == null || s.isEmpty()) return 0f;
            try {
                return Float.parseFloat(s);
            } catch (NumberFormatException e) {
                return 0f;
            }
        }

        public Vector3 value() {
            return new Vector3(
                toFloatSafe(fields[0].value),
                toFloatSafe(fields[1].value),
                toFloatSafe(fields[2].value)
            );
        }
        public void draw(SpriteBatch sb) {
            gameFont[2].draw(sb,label,position.x,position.y);
            for(TextField field : fields) {
                field.draw(sb);
            }
        }

        public void set(Vector3 values) {
            float[] valueArray = new float[] {values.x,values.y,values.z};
            for(int i = 0; i < 3; i++) {
                fields[i].set(valueArray[i] + "");
            }
        }
    }

    public static class Info {
        public Property pos, dir;
        public Rectangle bounds;
        public CameraTransform transform;
        public Rectangle cameraInfoBound;
        public Info(CameraTransform transform, Rectangle bounds, float yOffset) {
            this.bounds = bounds;
            this.transform = transform;
            pos = new Property("Pos",new float[] {transform.position.x,transform.position.y,transform.position.z}, new Vector2(bounds.x + 16, bounds.y - 16 + 80));
            dir = new Property("Dir",new float[] {transform.direction.x,transform.direction.y,transform.direction.z}, new Vector2(bounds.x + 16, bounds.y - 16 + 80 - 32));
            cameraInfoBound = new Rectangle(resolution.x/2f-24, bounds.y + yOffset,48,48);
        }
        public void draw(SpriteBatch sb) {
            pos.draw(sb);
            dir.draw(sb);
            if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && cursor.clicked(cameraInfoBound)) {
                pos.set(camera.position);
                dir.set(camera.direction);
            }
            sb.draw(icons[0],cameraInfoBound.x,cameraInfoBound.y,cameraInfoBound.width,cameraInfoBound.height);
        }
    }

    CameraTimelineWidget widget;
    TextField selectedTextField;

    public Info startInfo, endInfo;
//    public CameraTransitionMode mode = CameraTransitionMode.LINEAR;

    public Array<TextField> fields = new Array<>();
    Rectangle[] debugRect;
    public CinematicCameraModalDropdown dropdown;

    public CameraCinematicModal(CameraTimelineWidget widget) {
        this.widget = widget;

        startInfo = new Info(widget.startInfo, new Rectangle(resolution.x/2f-(resolution.x*0.7f)/2f,resolution.y - 175, resolution.x*0.7f,80),- 80);
        endInfo = new Info(widget.endInfo, new Rectangle(resolution.x/2f-(resolution.x*0.7f)/2f,100, resolution.x*0.7f,80),120);
        dropdown = new CinematicCameraModalDropdown(uiBG[0], uiBG[6], new Vector2(resolution.x/2f - 64,resolution.y - 375));

        debugRect = new Rectangle[]{startInfo.bounds, endInfo.bounds};

        fields.addAll(startInfo.pos.fields);
        fields.addAll(startInfo.dir.fields);
        fields.addAll(endInfo.pos.fields);
        fields.addAll(endInfo.dir.fields);
    }

    @Override
    public void save() {
        widget.startInfo.set(startInfo.pos.value(),startInfo.dir.value());
        widget.endInfo.set(endInfo.pos.value(),endInfo.dir.value());
        widget.mode = dropdown.currentMode;
    }

    @Override
    public Rectangle[] getDebugRect() {
        return debugRect;
    }

    @Override
    public void debug(SpriteBatch sb) {
        for(TextField field : fields) {
//            debugRectangle(field.bound,Color.YELLOW);
        }
    }

    @Override
    public void draw(SpriteBatch sb) {

        for(Rectangle rect : debugRect) {
            sb.draw(widgetBG,rect.x,rect.y,rect.width,rect.height);
        }

        startInfo.draw(sb);
        endInfo.draw(sb);
        dropdown.draw(sb);



        for (TextField field : fields) {
            field.hovered = cursor.clicked(field.bound);
            if(
                field.hovered
                && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
            ) {
                if(selectedTextField != null) {
                    selectedTextField.layout.setText(
                        gameFont[2],
                        selectedTextField.value,
                        Color.WHITE,
                        selectedTextField.bound.width,
                        Align.left,
                        true
                    );
                }
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
        if (selectedTextField == null) return false;

        if (character == '\b') {
            if (!selectedTextField.value.isEmpty()) {
                selectedTextField.value = selectedTextField.value.substring(0, selectedTextField.value.length() - 1);
            }
        } else {

            if ((character >= '0' && character <= '9') ||
                character == '.' ||
                character == '-') {

                if (character == '.' && selectedTextField.value.contains(".")) {
                    return false;
                }

                if (character == '-' && selectedTextField.value.length() > 0) {
                    return false;
                }

                selectedTextField.value += character;
            }
        }

        selectedTextField.layout.setText(
            gameFont[2],
            selectedTextField.value + "_",
            Color.WHITE,
            selectedTextField.bound.width,
            Align.left,
            true
        );

        return false;
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(cursor.clicked(dropdown.buttonRect) && button == Input.Buttons.LEFT) {
            dropdown.click();
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
