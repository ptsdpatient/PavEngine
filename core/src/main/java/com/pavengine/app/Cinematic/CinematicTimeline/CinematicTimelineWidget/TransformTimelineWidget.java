package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.Methods.transformObjectTransition;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicModal;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicModal.TransformCinematicModal;


public class TransformTimelineWidget extends CinematicTimelineWidget{

    public Array<TimelineTransformData> transformDataList = new Array<>();

    public TransformTimelineWidget() {
        super(cinematicPanel.selectedWidget.bg,
            cinematicPanel.selectedWidget.text,
            new Vector2(cinematicPanel.selectedWidget.lineRect.x - cinematicTimeline.scrollX,
                cinematicPanel.selectedWidget.lineRect.y - cinematicTimeline.scrollY),
            cinematicPanel.selectedWidget.type,
            cinematicTimeline.pixelsPerSecond);
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
            cinematicModal = new TransformCinematicModal(this);
        }

        if((time > startTime) && time < (startTime + duration)) {
            for(TimelineTransformData data : transformDataList) {
                if(time > startTime + data.delay && time < startTime + duration) {
                    transformObjectTransition( data.object, data.initialTransform, data.finalTransform, time,startTime + data.delay, duration);
                }
            }
        }

    }
}
