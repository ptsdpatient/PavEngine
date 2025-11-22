package com.pavengine.app.Cinematic.CinematicTimeline;

import static com.pavengine.app.Methods.extractSprites;
import static com.pavengine.app.Methods.load;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavScreen.CinematicEditor.playingScene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class CinematicTimeline {
    private final TextureRegion background, timelineMark;
    private final Array<CinematicTimelineObject> timelineObjects = new Array<>();
    public final Array<CinematicTimelineControl> timelineControls = new Array<>();
    public final Rectangle bounds;
    private final Rectangle timeLineBounds;

    private float scrollX = 0, scrollY = 0;
    private float size = 10, startX = 0, markSpacing = 0;
    public float timeSeconds = 0, timelinePointerX = 246;
    private final char[] timeBuffer = new char[8];

    public CinematicTimeline(TextureRegion background, TextureRegion timelineMark) {
        this.background = background;
        this.timelineMark = timelineMark;

        timelineObjects.add(new CameraTimelineObject("Camera", gameFont[2], uiBG[1], uiBG[3]));
        timelineObjects.add(new LightTimelineObject("Light", gameFont[2], uiBG[1], uiBG[3]));
        timelineObjects.add(new SubtitleTimelineObject("Subtitle", gameFont[2], uiBG[1], uiBG[3]));
        timelineObjects.add(new MusicTimelineObject("Music", gameFont[2], uiBG[1], uiBG[3]));
        timelineObjects.add(new TransformTimelineObject("Transform", gameFont[2], uiBG[1], uiBG[3]));
        timelineObjects.add(new AnimateTimelineObject("Animate", gameFont[2], uiBG[1], uiBG[3]));

        float posY = resolution.y / 2.5f - 70;
        for (CinematicTimelineObject obj : timelineObjects) {
            obj.setPosition(15, posY);
            posY -= 52;
        }

        bounds = new Rectangle(0, 0, resolution.x, resolution.y / 2.5f);
        timeLineBounds = new Rectangle(246, 0, resolution.x, resolution.y / 2.5f);

        int i = 0;
        for(TextureRegion tex : extractSprites("sprites/default/timeline_control.png",32,32)) {
            timelineControls.add(new CinematicTimelineControl(tex, new Vector2(15 + i * 48,resolution.y/2.5f - 50),i));
            i++;
        }

    }

    public void updateCursor() {
        timelinePointerX = startX + timeSeconds * markSpacing;
    }

    public void draw(SpriteBatch sb) {
        sb.draw(background, 0, 0, resolution.x, resolution.y / 2.5f);

        if (playingScene) {
            print(timeSeconds);
            timeSeconds += Gdx.graphics.getDeltaTime();
            updateCursor();
        }

        for (CinematicTimelineObject obj : timelineObjects) {
            if (obj.y + scrollY + 45 < resolution.y/2.5f)
                obj.draw(sb, scrollX, scrollY);
        }

        startX = scrollX + 256;
        markSpacing = (resolution.x - 256) / size;

        float baseY = resolution.y / 2.5f - 25;
        for (int i = 0; i < 200; i++) {
            float markX = startX + i * markSpacing;
            if (markX > 214)
                sb.draw(timelineMark, markX, baseY, 6, 18);
        }

        if (cursor.clicked(timeLineBounds) && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            timelinePointerX = cursor.cursor.getX();
            timeSeconds = Math.max((timelinePointerX - startX) / markSpacing, 0);
        }

        for (CinematicTimelineControl control : timelineControls)
            control.draw(sb);

        float pointerY = resolution.y / 2.5f - 48;
        sb.draw(timelineMark, timelinePointerX, 0, 6, pointerY);
        sb.draw(timelineMark, timelinePointerX - 82, pointerY, 164, 48); // center bar
        drawTime(sb);

    }

    private void drawTime(SpriteBatch sb) {
        int totalSeconds = (int) timeSeconds;
        int m = totalSeconds / 60;
        int s = totalSeconds % 60;
        int cs = (int) ((timeSeconds - totalSeconds) * 100);

        timeBuffer[0] = (char) ('0' + m / 10);
        timeBuffer[1] = (char) ('0' + m % 10);
        timeBuffer[2] = ':';
        timeBuffer[3] = (char) ('0' + s / 10);
        timeBuffer[4] = (char) ('0' + s % 10);
        timeBuffer[5] = ':';
        timeBuffer[6] = (char) ('0' + cs / 10);
        timeBuffer[7] = (char) ('0' + cs % 10);

        gameFont[2].draw(sb, String.valueOf(timeBuffer),
            timelinePointerX - 72, resolution.y / 2.5f - 12);
    }

    public void updateScrolling(float dx, float dy) {
        scrollX = MathUtils.lerp(scrollX, scrollX + dx, 0.18f);
        scrollY = MathUtils.lerp(scrollY, scrollY + dy, 0.18f);
        clampScroll();
    }

    private void clampScroll() {
        scrollX = Math.max(Math.min(scrollX, 0), -5000);
        scrollY = Math.max(Math.min(scrollY, timelineObjects.size * 45), 0);
        updateCursor();
    }

    public void resize(float amountY) {
        size = MathUtils.clamp(size + amountY * 0.5f, 0.3f, 14f);
        updateCursor();
    }
}
