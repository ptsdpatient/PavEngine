package com.pavengine.app.Cinematic.CinematicTimeline;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class LightTimelineObject extends CinematicTimelineObject {
    public LightTimelineObject(String name, BitmapFont fnt, TextureRegion nameTexture, TextureRegion lineTexture) {
        super(name, fnt, nameTexture, lineTexture, CinematicWidgetType.Light);
    }
}
