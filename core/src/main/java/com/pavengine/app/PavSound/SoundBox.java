package com.pavengine.app.PavSound;

import static com.pavengine.app.Methods.load;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;

import java.util.Objects;

public class SoundBox {
    public static boolean volume = true;
    public boolean isSound = true;
    public Array<Sounds> sounds = new Array<>();

    public void addSound(String name, Boolean isSound) {
        this.isSound = isSound;
        if (isSound) {
            sounds.add(new Sounds(name, Gdx.audio.newSound(load("sound/" + name))));
        } else {
            sounds.add(new Sounds(name, Gdx.audio.newMusic(load("music/" + name))));
        }
    }

    public void stopAll() {
        for (Sounds s : sounds) {
            if (s.isSound) {
                s.sound.stop();
            } else {
                s.music.stop();
            }

        }
    }

    public void dispose() {
        for (Sounds s : sounds) {
            s.sound.dispose();
            s.music.dispose();
        }
    }

    public void playSound(String name) {
        for (Sounds s : sounds) {

            if (Objects.equals(s.name, name)) {
                if (s.isSound) {
                    s.play(volume ? 1 : 0);
                } else s.play();
            }
        }
    }

    public void updateVolume() {
        for (Sounds s : sounds) {
            if (!s.isSound) {
                s.music.setVolume(volume ? 1 : 0);
            }
        }
    }
}

