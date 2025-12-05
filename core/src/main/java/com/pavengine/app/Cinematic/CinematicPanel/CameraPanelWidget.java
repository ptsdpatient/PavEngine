package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class CameraPanelWidget extends CinematicPanelWidget {

    public CameraPanelWidget(String name, Vector2 position) {
        super(name, position, uiBG[13],CinematicWidgetType.Camera);
    }

}
