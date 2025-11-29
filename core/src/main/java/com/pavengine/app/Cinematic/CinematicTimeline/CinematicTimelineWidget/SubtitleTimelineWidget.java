package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.subtitle;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavEngine.uiControl;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicModal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pavengine.app.Cinematic.CinematicModal.SubtitleCinematicModal;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class SubtitleTimelineWidget extends CinematicTimelineWidget{

    public String text = "Subtitle";
    public GlyphLayout layout = new GlyphLayout();

    public SubtitleTimelineWidget(TextureRegion bg, String text, Vector2 pixelPos, CinematicWidgetType type, float pixelsPerSecond) {
        super(bg, text, pixelPos, type, pixelsPerSecond);
        layout.setText(gameFont[2], text, Color.WHITE, resolution.x * 0.8f, Align.center, true);
    }

    @Override
    public void update(SpriteBatch sb, float time) {
        if(cursor.clicked(bounds) && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            cinematicModal = new SubtitleCinematicModal(uiBG[2],uiControl[5]);
        }
        if((time > startTime) && time < (startTime + duration)) {
            subtitle.draw(sb,layout);
        }
    }

}
