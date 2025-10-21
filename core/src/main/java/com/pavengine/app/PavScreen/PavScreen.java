package com.pavengine.app.PavScreen;

import static com.pavengine.app.GameInput.gameWorldInput;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.PavEngine.resolution;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pavengine.app.PavCursor;
import com.pavengine.app.PavEngine;

public abstract class PavScreen implements Screen {

    public static PavCursor cursor;
    public Vector2 resolution = new Vector2(1280,720);
    public PavEngine game;
    public FitViewport viewport;
    public SpriteBatch batch;
    public OrthographicCamera camera;

    public PavScreen(PavEngine game) {
        this.game = game;
        this.batch = game.batch;
        camera = new OrthographicCamera();
        viewport = new FitViewport(resolution.x, resolution.y, camera);
        camera.setToOrtho(false, resolution.x, resolution.y);
        viewport.apply();
        cursor = new PavCursor(
            "sprites/default/cursor_sheet.png",
            175f
        );

        lockCursor(false);

        resize((int) resolution.x, (int) resolution.y);

    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(gameWorldInput);
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        draw(delta);
        cursor.draw(batch, delta);
        batch.end();
    }

    public abstract void draw(float delta);


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        viewport.apply();
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {

    }
}
