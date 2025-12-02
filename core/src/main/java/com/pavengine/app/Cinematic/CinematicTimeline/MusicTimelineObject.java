package com.pavengine.app.Cinematic.CinematicTimeline;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class MusicTimelineObject extends CinematicTimelineObject{
    public MusicTimelineObject(String name, TextureRegion nameTexture) {
        super(name, nameTexture, CinematicWidgetType.Music);
    }
}
