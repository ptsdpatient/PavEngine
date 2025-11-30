package com.pavengine.app.Cinematic.CinematicTimeline;

import static com.pavengine.app.Methods.extractSprites;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;
import static com.pavengine.app.PavScreen.CinematicEditor.playingScene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.CinematicTimelineWidget;

public class CinematicTimeline {

    private final TextureRegion background, timelineMark;
    public Array<CinematicTimelineObject> timelineObjects = new Array<>();
    public Array<CinematicTimelineWidget> timelineWidgets = new Array<>();
    public final Array<CinematicTimelineControl> timelineControls = new Array<>();
    public final Rectangle bounds;
    private final Rectangle timeLineBounds;

    // Timeline scrolling and zoom
    public float scrollX = 0, scrollY = 0;
    public float pixelsPerSecond = 12f; // Zoom factor (changed via resize)
    private final float secondsPerMark = 10f; // Each marker = 10 seconds

    private float markSpacing = pixelsPerSecond * secondsPerMark;
    // Time tracking
    public float timeSeconds = 0;
    public float timelinePointerX = 246;
    public float startX = 256;

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
        for (TextureRegion tex : extractSprites("sprites/default/timeline_control.png", 32, 32)) {
            timelineControls.add(new CinematicTimelineControl(tex, new Vector2(15 + i * 48, resolution.y / 2.5f - 50), i));
            i++;
        }
        updateCursor();
    }

    public void updateCursor() {
        timelinePointerX = startX + timeSeconds * pixelsPerSecond;
    }

    public void draw(SpriteBatch sb) {
        sb.draw(background, 0, 0, resolution.x, resolution.y / 2.5f);

        if (playingScene) {
            timeSeconds += Gdx.graphics.getDeltaTime();
            updateCursor();
        }

        // Draw timeline objects (tracks)
        for (CinematicTimelineObject obj : timelineObjects) {
            if (obj.y + scrollY + 45 < resolution.y / 2.5f)
                obj.draw(sb, scrollX, scrollY);
        }

        // Define start position for markers
        startX = scrollX + 256;

        // --- Draw precise 10-second interval markers ---
        float baseY = resolution.y / 2.5f - 25;
        for (int i = 0; i < 500; i++) {
            float markX = startX + i * markSpacing;
            if (markX > 214 && markX < resolution.x + 600)
                sb.draw(timelineMark, markX, baseY, 6, 18);
        }

        // --- Mouse click to reposition timeline cursor accurately ---
        if ( (Gdx.input.getInputProcessor()==cinematicEditorInput) && !cinematicPanel.widgetDrag && cursor.clicked(timeLineBounds) && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && (cursor.index == 1 || cursor.index == 2)) {
            timelinePointerX = cursor.getX();
            timeSeconds = Math.max((timelinePointerX - startX) / pixelsPerSecond, 0);
        }

        // Draw controls
        for (CinematicTimelineControl control : timelineControls)
            control.draw(sb);

        // Draw widgets aligned with precise time positions
        for (CinematicTimelineWidget widget : timelineWidgets) {

            if (widget.trackY + scrollY + 80 < resolution.y / 2.5f)
                widget.draw(sb, timeSeconds, pixelsPerSecond, scrollY, scrollX, startX);
        }

        // Draw current time pointer
        float pointerY = resolution.y / 2.5f - 48;
        sb.draw(timelineMark, timelinePointerX, 0, 6, pointerY);
        sb.draw(timelineMark, timelinePointerX - 82, pointerY, 164, 48);

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

        gameFont[2].draw(sb, String.valueOf(timeBuffer), timelinePointerX - 72, resolution.y / 2.5f - 12);
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

    // 🔍 Zooming now affects only pixelsPerSecond, not time accuracy
    public void resize(float amountY) {
        pixelsPerSecond = MathUtils.clamp(pixelsPerSecond + amountY * 1.2f, 6f, 80f);
        markSpacing = pixelsPerSecond * secondsPerMark;
        updateCursor();
    }
}
