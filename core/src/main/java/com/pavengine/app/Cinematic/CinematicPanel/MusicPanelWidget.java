package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

public class MusicPanelWidget extends CinematicPanelWidget {

    public MusicPanelWidget(String name, Vector2 position, BitmapFont fnt) {
        super(name, position, uiBG[8],CinematicWidgetType.Music);
    }


}
