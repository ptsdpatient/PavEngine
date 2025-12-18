package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicModal;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicModal.SoundCinematicModal;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;


public class SoundTimelineWidget extends CinematicTimelineWidget{

    public Array<TimelineSoundData> soundDataList = new Array<>();
    boolean dataFalse = false;

    public SoundTimelineWidget() {
        super(
            cinematicPanel.selectedWidget.bg,
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
            cinematicModal = new SoundCinematicModal(this);
        }

        if (time < startTime && !dataFalse) {
            for (TimelineSoundData data : soundDataList) {
                data.played = false;
            }
            dataFalse = true;
        }

        if((time > startTime) && time < (startTime + duration)) {
            for(TimelineSoundData data : soundDataList) {
                if(time > startTime + data.delay && !data.played && time < startTime + data.delay + 0.5f) {
                    data.played = true;
                    dataFalse = false;
                    soundBox.playSound(data.sound);
                }
            }
        }
    }
}
