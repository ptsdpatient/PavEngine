package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;

public class TransformPanelWidget extends CinematicPanelWidget {

    public TransformPanelWidget(String name, Vector2 position) {
        super(name, position, uiBG[8], CinematicWidgetType.Transform);
    }

}
