package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.math.Vector2;

public class SoundPanelWidget extends CinematicPanelWidget {

    public SoundPanelWidget(String name, Vector2 position) {
        super(name, position, uiBG[8],CinematicWidgetType.Sound);
    }

}
