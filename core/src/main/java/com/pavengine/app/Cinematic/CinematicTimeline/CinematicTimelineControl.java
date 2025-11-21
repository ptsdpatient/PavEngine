package com.pavengine.app.Cinematic.CinematicTimeline;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class CinematicTimelineControl {
    public Sprite obj;
    float scale = 1f;
    public boolean hovered = false;
    public int index = 0;
    public CinematicTimelineControl(TextureRegion tex, Vector2 position, int index) {
        obj = new Sprite(tex);
        obj.setPosition(position.x, position.y);
        obj.setOriginCenter();
        obj.setSize(38,38);
        this.index = index;
    }

    public void update() {
        scale = MathUtils.lerp(scale, hovered ? 1.2f : 1f, 0.25f);
        obj.setScale(scale);
    }

    public void draw(SpriteBatch sb) {
        update();
        obj.draw(sb);
    }

}
