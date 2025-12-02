package com.pavengine.app.Cinematic.CinematicTimeline;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class SubtitleTimelineObject extends CinematicTimelineObject{
    public SubtitleTimelineObject(String name, TextureRegion nameTexture) {
        super(name, nameTexture, CinematicWidgetType.Subtitle);
    }
}
