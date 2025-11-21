package com.pavengine.app.PavScreen;


import static com.pavengine.app.Methods.files;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.PavInput.GameWorldInput.gameWorldInput;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pavengine.app.PavEngine;

public class LoadingScreen extends PavScreen {

    private TextureRegion[] images;
    private int currentIndex = 0;
    private float alpha = 0f;
    private boolean fadingIn = true;
    private boolean done = false;

    public LoadingScreen(PavEngine game) {
        super(game);
        String[] imageFiles = {
            "images/logo.png",
            "images/byog.jpeg",
            "images/theme.png",
        };

        images = new TextureRegion[imageFiles.length];
        for (int i = 0; i < imageFiles.length; i++) {
            images[i] = new TextureRegion(new Texture(files(imageFiles[i])));
        }
    }


    @Override
    public void setInput() {
        Gdx.input.setInputProcessor(gameWorldInput);
    }


    @Override
    public void debug() {

    }

    @Override
    public void draw(float delta) {
        batch.setColor(1, 1, 1, alpha);

        if (!done) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                currentIndex++;
                if (currentIndex >= images.length) {
                    done = true;
                    PavEngine.enableCursor = false;
                    PavEngine.gamePause = false;
                    game.setGameScreen();
                    return;
                }
                alpha = 0f;
                fadingIn = true;
            }

            float fadeSpeed = 1.5f;
            if (fadingIn) {
                alpha += delta / fadeSpeed;
                if (alpha >= 1f) {
                    alpha = 1f;
                    fadingIn = false;
                }
            } else {
                alpha -= delta / fadeSpeed;
                if (alpha <= 0f) {
                    alpha = 0f;
                    fadingIn = true;
                    currentIndex++;
                    if (currentIndex >= images.length) {
                        done = true;
                        PavEngine.enableCursor = false;
                        PavEngine.gamePause = false;
                        lockCursor(true);
                        game.setGameScreen();
                        return;
                    }
                }
            }
        }

        batch.draw(images[currentIndex], 0, 0, resolution.x, resolution.y);


        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void world(float delta) {

    }



}

