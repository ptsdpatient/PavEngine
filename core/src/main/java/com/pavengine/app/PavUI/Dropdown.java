package com.pavengine.app.PavUI;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.pavengine.app.ObjectType;

public class Dropdown {
    public BitmapFont font;
    public String[] options;
    public int selectedIndex;
    public Rectangle box;
    public boolean expanded = false;
    public WidgetType type;
    GlyphLayout layout;
    float spriteWidth = 192, spriteHeight = 48;
    Sprite background;
    private Vector3 overlayTouch = new Vector3();

    public Dropdown(float x, float y, String[] options, int selectedIndex, BitmapFont font) {
        this.options = options;
        this.selectedIndex = Math.max(0, Math.min(selectedIndex, options.length - 1));
        this.font = font;

        layout = new GlyphLayout(font, options[this.selectedIndex]);
        this.box = new Rectangle(x, y, spriteWidth, spriteHeight);

        if (background != null) {
            background.setPosition(x, y);
            background.setSize(spriteWidth, spriteHeight);
        }
        type = WidgetType.TextBox;
    }

    public void clicked(Vector2 point) {
        if (box.contains(point)) {
            expanded = !expanded;
            return;
        }
        if (expanded) {
            for (int i = 0; i < options.length; i++) {
                Rectangle optBox = new Rectangle(box.x, box.y + (i + 1) * spriteHeight, spriteWidth, spriteHeight);
                if (optBox.contains(point)) {
                    selectedIndex = i;
                    print(selectedIndex);
                    if (selectedObject != null) {
                        print(selectedObject.objectType + "");

                        switch (i) {
                            case 0: {

                                selectedObject.objectType = ObjectType.STATIC;


                            }
                            ;
                            break;
                            case 3: {
                                selectedObject.objectType = ObjectType.KINEMATIC;
                            }
                            ;
                            break;
                            case 2: {
                                selectedObject.objectType = ObjectType.GROUND;
                            }
                            ;
                            break;
                            case 1: {
                                selectedObject.objectType = ObjectType.TARGET;
                            }
                            ;
                            break;
                        }
                        print(selectedObject.objectType + "");

                    }
                    expanded = false;
                    return;
                }
            }
        }
    }

    public void update() {
        if (background != null) {
            background.setBounds(box.x, box.y, box.width, box.height);
        }
    }

    public void render(SpriteBatch sb) {
        // Draw main box
        if (background != null) background.draw(sb);

        layout.setText(font, options[selectedIndex]);
        float textX = box.x + 10;
        float textY = box.y + (box.height + layout.height) / 2f;
        font.draw(sb, layout, textX, textY);

        // Draw expanded options
        if (expanded) {
            for (int i = 0; i < options.length; i++) {
                float optY = box.y + (i + 1) * spriteHeight;
                Rectangle optBox = new Rectangle(box.x, optY, spriteWidth, spriteHeight);
                if (background != null) {
                    background.setBounds(optBox.x, optBox.y, optBox.width, optBox.height);
                    background.draw(sb);
                }
                layout.setText(font, options[i]);
                font.draw(sb, layout, textX, optY + (spriteHeight + layout.height) / 2f);
            }
        }

        // Handle input
        overlayTouch.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        overlayViewport.unproject(overlayTouch);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            clicked(new Vector2(overlayTouch.x, overlayTouch.y));
        }
    }

    public String getSelected() {
        return options[selectedIndex];
    }
}
