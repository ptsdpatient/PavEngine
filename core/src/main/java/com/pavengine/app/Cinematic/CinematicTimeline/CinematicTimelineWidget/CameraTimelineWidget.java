package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.Methods.transitionCamera;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicModal;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.pavengine.app.TransformTransition;
import com.pavengine.app.CameraTransitionMode;
import com.pavengine.app.Cinematic.CinematicModal.CameraCinematicModal;

public class CameraTimelineWidget extends CinematicTimelineWidget{

    public GlyphLayout layout = new GlyphLayout();
    public TransformTransition startInfo = new TransformTransition(), endInfo = new TransformTransition();
    public CameraTransitionMode mode = CameraTransitionMode.LINEAR;

    public CameraTimelineWidget() {
        super(
            cinematicPanel.selectedWidget.bg,
            cinematicPanel.selectedWidget.text,
            new Vector2(cinematicPanel.selectedWidget.lineRect.x - cinematicTimeline.scrollX,
                cinematicPanel.selectedWidget.lineRect.y - cinematicTimeline.scrollY),
            cinematicPanel.selectedWidget.type,
            cinematicTimeline.pixelsPerSecond);
        layout.setText(gameFont[2], text, Color.WHITE, resolution.x * 0.8f, Align.center, true);
    }

    @Override
    public void delete() {
        Gdx.input.setInputProcessor(cinematicEditorInput);
        cinematicTimeline.timelineWidgets.removeValue(this,true);
        cinematicModal = null;
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
