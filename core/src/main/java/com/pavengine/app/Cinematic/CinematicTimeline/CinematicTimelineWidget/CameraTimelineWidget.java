package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.Methods.transitionCamera;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.subtitle;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicModal;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pavengine.app.CameraTransform;
import com.pavengine.app.CameraTransitionMode;
import com.pavengine.app.Cinematic.CinematicModal.CameraCinematicModal;
import com.pavengine.app.Cinematic.CinematicModal.SubtitleCinematicModal;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class CameraTimelineWidget extends CinematicTimelineWidget{

    public GlyphLayout layout = new GlyphLayout();
    public CameraTransform startInfo = new CameraTransform(), endInfo = new CameraTransform();
    public CameraTransitionMode mode = CameraTransitionMode.LINEAR;

    public CameraTimelineWidget(TextureRegion bg, String text, Vector2 pixelPos, CinematicWidgetType type, float pixelsPerSecond) {
        super(bg, text, pixelPos, type, pixelsPerSecond);
        layout.setText(gameFont[2], text, Color.WHITE, resolution.x * 0.8f, Align.center, true);
    }

    @Override
    public void delete() {
        cinematicTimeline.timelineWidgets.removeValue(this,true);
    }

    @Override
    public void update(SpriteBatch sb, float time) {
        if(cursor.clicked(bounds) && Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            cinematicModal = new CameraCinematicModal(this);
        }
        if((time >= startTime) && time <= (startTime + duration)) {
            transitionCamera(startInfo,endInfo,time - startTime,duration,mode);
        }
    }
}
