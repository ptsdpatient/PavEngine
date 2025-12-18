package com.pavengine.app.Cinematic.CinematicTimeline;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class SoundTimelineObject extends CinematicTimelineObject{
    public SoundTimelineObject(String name, TextureRegion nameTexture) {
        super(name, nameTexture, CinematicWidgetType.Sound);
    }
}
