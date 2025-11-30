package com.pavengine.app.Cinematic.CinematicModal;

import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.resolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;

public class SubtitleCinematicModal extends CinematicModal {

    class ColorSubtitle {
        String subtitleText = "Aa";
        Color subtitleColor;
        Vector2 position;
        GlyphLayout layout;
        Rectangle bound;

        ColorSubtitle(Color color, Vector2 position) {
            this.subtitleColor = color;
            this.position = position;
            this.layout = new GlyphLayout(gameFont[2], subtitleText, color, 64, Align.center, false);

            float boundX = position.x;
            float boundY = position.y - layout.height;
            bound = new Rectangle(boundX + 12, boundY - 8, layout.width, layout.height + 16);
        }

        public boolean hovered() {
            return cursor.clicked(bound);
        }

        public boolean clicked() {
            return hovered() && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        }

        public void draw(SpriteBatch sb) {
            // Draw background only when hovered
            if (hovered() || this == selectedColorSubtitle) {
                sb.draw(accentBG, bound.x, bound.y, bound.width, bound.height);
                if(Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)){
                    selectedColorSubtitle = this;
                    color = subtitleColor;
                    textLayout.setText(gameFont[2], text + "_" ,color, textArea.width, Align.left,true);
                }

            }

            // Draw text - baseline position stays same
            gameFont[2].draw(sb, layout, position.x, position.y);
        }
    }



    GlyphLayout textLayout;
    String text;
    Color color;
    float blinkTimer = 0f;
    boolean textAreaActive = false,cursorBlink = false;
    Color[] colorList = new Color[]{
        Color.WHITE,       // Default subtitle
        Color.YELLOW,      // Common for translation or emphasis
        Color.GOLD,        // More elegant yellow
        Color.CYAN,        // Calm and readable
        Color.SKY,         // Softer blue
        Color.LIME,        // Highlight speech/character
        Color.ORANGE,      // Good for warnings/urgent
        Color.SALMON,      // Soft red for emotional tone
        Color.MAGENTA,     // Special narrator/dialogue
        Color.LIGHT_GRAY,   // Whisper / low volume
        new Color(0.6f, 0f, 0f, 1f),      // DARK_RED - Horror monster / evil / danger
        new Color(0.5f, 0f, 0.5f, 1f),    // PURPLE - Magic / mysterious / psychic
        new Color(0.13f, 0.55f, 0.13f, 1f),// FOREST - Wise elder / nature spirit
        new Color(0.68f, 0.85f, 0.9f, 1f) // LIGHT_BLUE - Robot / AI / ghost / calm
    };
    Array<ColorSubtitle> colorSubtitles = new Array<>();
    ColorSubtitle selectedColorSubtitle;
    public Rectangle textArea = new Rectangle(resolution.x/2f-(resolution.x*0.6f)/2f,400, resolution.x*0.6f,100);
    public Rectangle colorArea = new Rectangle(resolution.x/2f-(resolution.x*0.4f)/2f,255, resolution.x*0.4f,100);
    Rectangle[] debugRect = new Rectangle[]{ textArea, colorArea };

    public SubtitleCinematicModal(String text, Color color) {
        this.text = text;
        this.color = color;
        textLayout = new GlyphLayout(gameFont[2], text  + "_",color, textArea.width, Align.left,true);
        int i = 0, j = 0;
        for(Color colorSample : colorList) {
            colorSubtitles.add(new ColorSubtitle(colorSample,new Vector2(i + colorArea.x +16, j + colorArea.y + colorArea.height - 16)));
            if(colorSubtitles.peek().subtitleColor == color) {
                selectedColorSubtitle = colorSubtitles.peek();
            }
            i+=66;
            if(i > colorArea.width - 66) {
                i = 0;
                j -= 40;
            }
        }
    }

    @Override
    public Rectangle[] getDebugRect() {
        return debugRect;
    }

    @Override
    public void draw(SpriteBatch sb) {
        blinkTimer += Gdx.graphics.getDeltaTime();

        for(Rectangle rect : debugRect) {
            sb.draw(widgetBG,rect.x,rect.y,rect.width,rect.height);
        }

        if(blinkTimer > 0.4) {
            cursorBlink = !cursorBlink;
            blinkTimer = 0;
        }

        if(textAreaActive && cursorBlink) {
//            sb.draw(accentBG,textArea.x + textLayout.width + 3,textArea.y + 8,16,5);
        }

//        gameFont[1].draw(sb,textLayout,textArea.x + 16, textArea.y + 16 + textLayout.height);

        for(ColorSubtitle colorSubtitle : colorSubtitles) {
            colorSubtitle.draw(sb);
        }

        sb.flush();
        Rectangle scissor = new Rectangle();
        ScissorStack.calculateScissors(overlayCamera, sb.getTransformMatrix(), textArea, scissor);
        ScissorStack.pushScissors(scissor);

        gameFont[1].draw(sb, textLayout, textArea.x + 16, textArea.y + 16 + textLayout.height);

        sb.flush();
        ScissorStack.popScissors();


        if(cursor.clicked(textArea) && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
            textAreaActive= !textAreaActive;
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

        textLayout.setText(gameFont[2], text + "_" ,color, textArea.width, Align.left,true);
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
