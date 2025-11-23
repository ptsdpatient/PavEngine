package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineObject;

public class CinematicPanelSelectedWidget {

    public String text;
    public TextureRegion bg;
    public Vector2 offset;
    public Rectangle lineRect = new Rectangle();
    public boolean snapping = false;
    public CinematicWidgetType type;

    public CinematicPanelSelectedWidget() {

    }

    public void set(TextureRegion bg, String text, Vector2 offset, CinematicWidgetType type) {
        this.bg = bg;
        this.text = text;
        this.offset = offset;
        this.type = type;
        print(offset+"");
    }

    public void draw(SpriteBatch batch, Vector2 position) {

        lineRect.set(position.x + offset.x, position.y + offset.y +6, 224, 32);

        snapping = false;

        for (CinematicTimelineObject line : cinematicTimeline.timelineObjects) {

            if (line.lineRect.overlaps(lineRect) && line.type==type) {

                float targetCenterY = line.lineRect.y + line.lineRect.height / 2;
                float myCenterOffset = (40f / 2f);

                position.y = targetCenterY - myCenterOffset - offset.y;

                lineRect.setY(position.y + offset.y);

                snapping = true;
                break;
            }
        }

        batch.draw(bg, position.x + offset.x, position.y + offset.y, 224, 40);
        gameFont[2].draw(batch, text, position.x + offset.x + 10, position.y + offset.y + 28);
    }


}
