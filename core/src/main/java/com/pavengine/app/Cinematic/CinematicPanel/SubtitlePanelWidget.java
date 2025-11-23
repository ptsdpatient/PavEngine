package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class SubtitlePanelWidget extends CinematicPanelWidget {

    public SubtitlePanelWidget(String name, BitmapFont fnt) {
        super(name, fnt, uiBG[8],CinematicWidgetType.Subtitle);
    }

}
