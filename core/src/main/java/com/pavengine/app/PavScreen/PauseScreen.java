package com.pavengine.app.PavScreen;

import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavUI.PavAnchor.CENTER;
import static com.pavengine.app.PavUI.PavFlex.ROW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavSound.SoundBox;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.TextButton;

public class PauseScreen implements Screen {

    public static Vector2 screen = new Vector2(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    public static FitViewport viewport;
    private final SpriteBatch batch;
    public PavEngine game;
    public SoundBox soundBox = new SoundBox();
    public OrthographicCamera camera;
    private PavLayout pauseLayout;

    public PauseScreen(PavEngine game) {
        this.game = game;
        this.batch = game.batch;


        camera = new OrthographicCamera();
        viewport = new FitViewport(resolution.x, resolution.y, camera);
        camera.setToOrtho(false, resolution.x, resolution.y);


        viewport.apply();


        pauseLayout = new PavLayout(CENTER, ROW, 4, 164, 32);
        pauseLayout.addSprite(new TextButton("PAUSE (Click to go back)", gameFont[3], ClickBehavior.Nothing));
        resize((int) screen.x, (int) screen.y);

    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0, 1f);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        if (
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
        ) {
            game.setGameScreen();
        }
        batch.begin();
        pauseLayout.draw(batch, viewport.getWorldWidth(), viewport.getWorldHeight());
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);

        viewport.apply();
    }

    @Override
    public void show() {
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
        soundBox.stopAll();
    }

    @Override
    public void dispose() {
        batch.dispose();
//        image.getTexture().dispose();
    }


}

