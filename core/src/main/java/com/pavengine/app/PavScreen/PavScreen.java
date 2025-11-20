package com.pavengine.app.PavScreen;

import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.cameraBehavior;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.pavCamera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pavengine.app.CameraBehaviorType;
import com.pavengine.app.PavCamera.PavCamera;
import com.pavengine.app.PavCursor;
import com.pavengine.app.PavEngine;

public abstract class PavScreen implements Screen {


    public Vector2 resolution = new Vector2(1280,720);
    public PavEngine game;
    public SpriteBatch batch;
    private OrthographicCamera camera;
    private FitViewport overlayViewport, perspectiveViewport;


    public PavScreen(PavEngine game) {
        this.game = game;
        this.batch = game.batch;

        this.camera = PavEngine.overlayCamera;
        this.overlayViewport = PavEngine.overlayViewport;
        this.perspectiveViewport = PavEngine.perspectiveViewport;

        resize((int) resolution.x, (int) resolution.y);

    }

    @Override
    public void show() {
        setInput();
    }


    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        world(delta);

        camera.update();

        batch.setProjectionMatrix(camera.combined);

        batch.begin();

        if (
            !(cameraBehavior == CameraBehaviorType.MapEditorCamera || cameraBehavior == CameraBehaviorType.Cinematic &&
                (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE) || Gdx.input.isButtonPressed(Input.Buttons.RIGHT))
            )
        )
            cursor.draw(batch, delta);

        draw(delta);


        batch.end();

        debug();

//        debugRectangle(PavCursor.clickArea, Color.CYAN);
//        print(PavCamera.camera.position);
//        print(PavCamera.camera.direction);

    }

    public abstract void setInput();

    public abstract void debug();

    public abstract void draw(float delta);

    public abstract void world(float delta);

    @Override
    public void resize(int width, int height) {

        overlayViewport.update(width, height, true);
        overlayViewport.apply();

        perspectiveViewport.update(width, height,false);
        perspectiveViewport.apply();
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
