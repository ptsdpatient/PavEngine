package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.subtitle;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicModal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.Cinematic.CinematicModal.AnimateCinematicModal;
import com.pavengine.app.Cinematic.CinematicModal.SubtitleCinematicModal;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class AnimateTimelineWidget extends CinematicTimelineWidget{
    public AnimateTimelineWidget(TextureRegion bg, String text, Vector2 pixelPos, CinematicWidgetType type, float pixelsPerSecond) {
        super(bg, text, pixelPos, type, pixelsPerSecond);
    }

    @Override
    public void update(SpriteBatch sb, float time) {
        if(cursor.clicked(bounds) && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            cinematicModal = new AnimateCinematicModal(this);
        }
        if((time > startTime) && time < (startTime + duration)) {

        }
    }
}
