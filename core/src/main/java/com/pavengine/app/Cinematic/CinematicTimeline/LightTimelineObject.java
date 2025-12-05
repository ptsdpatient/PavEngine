package com.pavengine.app.Cinematic.CinematicTimeline;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class LightTimelineObject extends CinematicTimelineObject {
    public LightTimelineObject(String name, TextureRegion nameTexture) {
        super(name, nameTexture, CinematicWidgetType.Light);
    }
}
