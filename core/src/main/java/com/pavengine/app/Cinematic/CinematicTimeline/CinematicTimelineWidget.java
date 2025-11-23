package com.pavengine.app.Cinematic.CinematicTimeline;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class CinematicTimelineWidget {

    String text;
    TextureRegion bg;
    public Vector2 position;
    public CinematicWidgetType type;

    public CinematicTimelineWidget(TextureRegion bg, String text, Vector2 position, CinematicWidgetType type) {
        this.bg = bg;
        this.text = text;
        this.position = position;
        this.type = type;
    }


    public void draw(SpriteBatch batch, float width) {
        batch.draw(bg, position.x + this.position.x, position.y + this.position.y, width, 40);
        gameFont[2].draw(batch, text, position.x + this.position.x + 10, position.y + this.position.y + 28);
    }

}
