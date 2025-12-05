package com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget;

import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public abstract class CinematicTimelineWidget {
    public float duration = 10f; // stays in seconds
    public float startTime;      // NEW: time-based position
    public float trackY;         // store row Y (unchanged by resize)
    public Vector2 offsetDrag;
    TextureRegion bg;
    public Rectangle leftRectangle, rightRectangle, bounds;
    String text;
    public CinematicWidgetType type;
    public float pixelX, yPos, width, rightX;

    public CinematicTimelineWidget(TextureRegion bg, String text, Vector2 pixelPos, CinematicWidgetType type, float pixelsPerSecond) {
        this.bg = bg;
        this.text = text;
        this.type = type;
        leftRectangle = new Rectangle(0,0,32,40);
        rightRectangle = new Rectangle(0,0,32,40);
        bounds = new Rectangle(0,0,32,40);
        offsetDrag = new Vector2();
        // Convert screen position to time accurately
        this.startTime = (pixelPos.x - 256) / pixelsPerSecond;
        this.trackY = pixelPos.y;
    }

    public abstract void update(SpriteBatch sb, float time);

    public void draw(SpriteBatch batch, float time, float pps, float scrollY, float scrollX, float startX) {
        update(batch,time);
        pixelX = startX + scrollX + startTime * pps;
        yPos = trackY + scrollY;
        width = duration * pps;
        rightX = pixelX + width - 16f;

        bounds.set(pixelX + 16f, yPos, width - 32f, 40f);
        batch.draw(bg, pixelX , yPos, width, 40f);

        leftRectangle.setPosition(pixelX - 16f, yPos);
        rightRectangle.setPosition(rightX, yPos);
    }
}

