package com.pavengine.app.Cinematic;

import static com.pavengine.app.Methods.load;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

public class CinematicTimeline {
    TextureRegion background, timelineMark;
    Array<CinematicTimelineObject> timelineObjects = new Array<>();
    public Rectangle bounds, timeLineBounds;

    BitmapFont timelineCursorFont;
    float scrollX = 0;
    float scrollY = 0;

    GlyphLayout layout = new GlyphLayout();
    float size = 10;
    private final char[] timeBuffer = new char[8];

    public CinematicTimeline(TextureRegion background,TextureRegion timelineMark) {
        this.background = background;
        this.timelineMark = timelineMark;

        timelineObjects.add(new CinematicTimelineObject("Camera", gameFont, uiBG[2], uiBG[3]));
        timelineObjects.add(new CinematicTimelineObject("Light", gameFont, uiBG[2], uiBG[3]));
        timelineObjects.add(new CinematicTimelineObject("Animation", gameFont, uiBG[2], uiBG[3]));

        float posY = resolution.y / 2.5f - 70;
        for (CinematicTimelineObject obj : timelineObjects) {
            obj.setPosition(15, posY);
            posY -= 52;
        }
        bounds = new Rectangle(0,0,resolution.x, resolution.y / 2.5f);
        timeLineBounds = new Rectangle(196 + 50, 0,resolution.x, resolution.y/2.5f);
        timelineCursorFont = new BitmapFont(load("font/default/cinematic/timeline.fnt"));
    }

    public void draw(SpriteBatch sb) {
        sb.draw(background, 0, 0, resolution.x, resolution.y / 2.5f);

        // Draw Cinematic Objects
        for (CinematicTimelineObject obj : timelineObjects) {
            obj.draw(sb, scrollX, scrollY);
        }

        float startX = scrollX + 196 + 60;
        float markSpacing = (resolution.x - 256) / size;

        for (int i = 0; i < 200; i++) {
            float markX = startX + i * markSpacing;
            if(markX > timeLineBounds.x-32)
                sb.draw(timelineMark, markX, resolution.y / 2.5f - 25, 6, 18);

        }

        if (cursor.clicked(timeLineBounds)) {

            float cursorX = cursor.cursor.getX();
            sb.draw(timelineMark, cursorX, 0, 6, resolution.y / 2.5f - 48);
            sb.draw(timelineMark, cursorX - 164/2f, resolution.y / 2.5f - 48, 164, 48);

            float timeSeconds = (cursorX - startX - scrollX) / markSpacing;
            timeSeconds = Math.max(timeSeconds, 0);

            int totalSeconds = (int) timeSeconds;
            int m = totalSeconds / 60;
            int s = totalSeconds % 60;
            int cs = (int) ((timeSeconds - totalSeconds) * 100);

            String currentTime = buildTimeString(m, s, cs);

            timelineCursorFont.draw(sb, currentTime, cursorX -  164/2f +10,
                resolution.y / 2.5f - 12);
        }
    }

    private String buildTimeString(int m, int s, int cs) {

        timeBuffer[0] = (char) ('0' + m / 10);
        timeBuffer[1] = (char) ('0' + m % 10);
        timeBuffer[2] = ':';
        timeBuffer[3] = (char) ('0' + s / 10);
        timeBuffer[4] = (char) ('0' + s % 10);
        timeBuffer[5] = ':';
        timeBuffer[6] = (char) ('0' + cs / 10);
        timeBuffer[7] = (char) ('0' + cs % 10);

        return String.valueOf(timeBuffer);
    }

    public void updateScrolling(float deltaX, float deltaY) {
        scrollX = MathUtils.lerp(scrollX, scrollX + deltaX, 0.15f);
        scrollY = MathUtils.lerp(scrollY, scrollY + deltaY, 0.15f);
        clampScroll();
    }

    private void clampScroll() {
        scrollX = Math.max(Math.min(scrollX, 0), - 5000);
        scrollY = Math.max(scrollY,0);
    }

    public void resize(float amountY) {
        size = MathUtils.lerp(size, size + amountY, 0.8f);
        size = Math.min(Math.max(size,0.3f),14);
        print(size);
    }
}
