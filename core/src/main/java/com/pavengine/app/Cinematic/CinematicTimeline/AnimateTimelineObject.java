package com.pavengine.app.Cinematic.CinematicTimeline;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class AnimateTimelineObject extends CinematicTimelineObject{
    public AnimateTimelineObject(String name, BitmapFont fnt, TextureRegion nameTexture, TextureRegion lineTexture) {
        super(name, fnt, nameTexture, lineTexture, CinematicWidgetType.Animate);
    }
}
