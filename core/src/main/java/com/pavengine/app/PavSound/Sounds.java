package com.pavengine.app.PavSound;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Sounds {
    public Music music;
    public Sound sound;
    public String name;
    public Boolean isSound;

    public Sounds(String name, Sound sound) {
        this.name = name;
        this.sound = sound;
        isSound = true;
    }

    public Sounds(String name, Music music) {
        this.name = name;
        this.music = music;
        this.music.setLooping(true);
        this.music.setVolume(0.5f);
        isSound = false;
    }

    public void play() {
        music.play();
    }

    public void play(float volume) {
        sound.play(1);
    }
}
