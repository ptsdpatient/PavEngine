package com.pavengine.app.PavUI;

import static com.pavengine.app.PavEngine.enableMapEditor;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameWorld.overlayViewport;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Stepper {
    public Sprite stepUp, stepDown;
    public BitmapFont font, subtitle;
    public String text;
    public Rectangle box;
    public Sprite background, image;
    public boolean isHovered = false;
    public ClickBehavior clickBehavior = ClickBehavior.Nothing;
    public Vector3 value;
    public WidgetType type;
    GlyphLayout layout;
    float spriteWidth = 192, spriteHeight = 64;
    private Vector3 overlayTouch = new Vector3();

    public Stepper(float x, float y, Vector3 value, ClickBehavior clickBehavior, String text, BitmapFont font, TextureRegion stepUpTexture, TextureRegion stepDownTexture) {
        this.text = text;
        this.font = font;
        layout = new GlyphLayout(font, text);
        this.value = value;
        this.box = new Rectangle(0, 0, layout.width, layout.height);
        this.stepUp = new Sprite(stepUpTexture);
        this.stepDown = new Sprite(stepDownTexture);
        this.clickBehavior = clickBehavior;

        box.setPosition(x, y);

        if (background != null) background.setPosition(x, y);

        box.setSize(spriteWidth, spriteHeight);
        if (background != null) background.setSize(spriteWidth, spriteHeight);

        stepUp.setPosition(box.x + box.width + 3f - stepUp.getWidth(), box.y + box.height / 2f);
        stepDown.setPosition(box.x + box.width + 3f - stepUp.getWidth(), box.y);

        stepUp.setSize(spriteHeight / 2f, spriteHeight / 2f);
        stepDown.setSize(spriteHeight / 2f, spriteHeight / 2f);

        type = WidgetType.Stepper;
    }

    public Vector2 getCenter() {
        return new Vector2(box.x + box.width / 2f, box.y + box.height / 2f);
    }

    public boolean clicked(Vector2 point) {
        return box.contains(point);
    }


    public void update() {
        // Placeholder: you could add animations, scaling, etc.
        if (background != null) background.setBounds(box.x, box.y, box.width, box.height);
    }


    public void render(SpriteBatch sb) {
        if (background != null) background.draw(sb);

        layout = new GlyphLayout(font, text);
        float textX = box.x + stepUp.getWidth() / 2f;
        float textY = box.y + (box.height + layout.height) / 2f;

        font.draw(sb, layout, textX, textY);
        stepUp.draw(sb);
        stepDown.draw(sb);

        overlayTouch = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        overlayViewport.unproject(overlayTouch);
        if (enableMapEditor) {
            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (selectedObject == null) return;
                switch (clickBehavior) {
                    case StepperRotation: {
                        if (stepUp.getBoundingRectangle().contains(overlayTouch.x, overlayTouch.y)) {
                            selectedObject.rotation.setEulerAngles(selectedObject.rotation.getYaw() + value.x, selectedObject.rotation.getRoll() + value.y, selectedObject.rotation.getPitch() + value.z);
                        }
                        if (stepDown.getBoundingRectangle().contains(overlayTouch.x, overlayTouch.y)) {
                            selectedObject.rotation.setEulerAngles(selectedObject.rotation.getYaw() - value.x, selectedObject.rotation.getRoll() - value.y, selectedObject.rotation.getPitch() - value.z);
                        }
                    }
                    ;
                    break;
                    case StepperScale: {
                        if (stepUp.getBoundingRectangle().contains(overlayTouch.x, overlayTouch.y)) {
                            selectedObject.size.add(value);
                        }
                        if (stepDown.getBoundingRectangle().contains(overlayTouch.x, overlayTouch.y)) {
                            selectedObject.size.sub(value);
                        }
                        clampMin(selectedObject.size, 0.1f);
                    }
                    ;
                    break;
                    case StepperElevation: {
                        if (stepUp.getBoundingRectangle().contains(overlayTouch.x, overlayTouch.y)) {
                            selectedObject.pos.add(value);
                        }
                        if (stepDown.getBoundingRectangle().contains(overlayTouch.x, overlayTouch.y)) {
                            selectedObject.pos.sub(value);
                        }
                        clampMin(selectedObject.size, 0.1f);
                    }
                    ;
                    break;

                }
            }
        }

//        debugRectangle(box, Color.BLUE);
    }

    private void clampMin(Vector3 v, float min) {
        v.x = Math.max(min, v.x);
        v.y = Math.max(min, v.y);
        v.z = Math.max(min, v.z);
    }

    public void setSize(float spriteWidth, float spriteHeight) {
    }

    public void setPosition(float x, float y) {

    }
}
