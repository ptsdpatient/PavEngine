package com.pavengine.app;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class GameSprite {
    Sprite obj;

    public GameSprite(TextureRegion tex) {
//        this.obj = new Sprite(tex);
//        obj.setOriginCenter();
    }

    public GameSprite(FileHandle file) {
//        this.obj = new Sprite(new TextureRegion(new Texture(file)));
//        obj.setOriginCenter();
//        obj.setScale(0.5f);
    }

    public void render(SpriteBatch sb) {
        obj.draw(sb);
    }
}
