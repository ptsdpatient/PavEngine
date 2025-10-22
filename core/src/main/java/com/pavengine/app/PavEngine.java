package com.pavengine.app;

import static com.pavengine.app.Methods.extractSprites;
import static com.pavengine.app.Methods.load;
import static com.pavengine.app.Methods.lockCursor;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.PavScreen.GameScreen;
import com.pavengine.app.PavScreen.LoadingScreen;
import com.pavengine.app.PavScreen.PauseScreen;
import com.pavengine.app.PavScreen.UpgradeScreen;
import com.pavengine.app.PavSound.SoundBox;


public class PavEngine extends Game {
    public static boolean dragAndDrop = false;
    public static boolean enableCursor = true;
    public static boolean enableMapEditor = false;
    public static boolean gamePause = false;
    public static Vector2 resolution = new Vector2(1280, 720);
    public static float sprayTime = 0f, sprayLimit = 5f, playerDamageTime = 0f, playerDamage = 30f, playerDamageRate = 0.3f, sprayCooldown = 0f, blastRadius = 5f;
    public static boolean levelStatus = false;
    public static float credits = 10f, health = 100f;
    public static SoundBox soundBox = new SoundBox();
    public GameScreen gameScreen;
    public LoadingScreen loadingScreen;
    public UpgradeScreen upgradeScreen;
    public PauseScreen pauseScreen;
    public SpriteBatch batch;

    public static BitmapFont
        gameFont,
        bigGameFont ;

    public static TextureRegion[]
        uiBG ,
        uiControl,
        hoverUIBG;

    public String[] soundList = new String[]{
        "/winning/1.mp3", "/winning/2.mp3", "/winning/3.mp3", "/loss/1.mp3", "/loss/2.mp3", "/loss/3.mp3", "intro.mp3",
        "turret_1.mp3", "turret_2.wav", "rail_move.wav", "turret_reload.wav", "robot_damage.wav", "robot_damage_1.mp3", "robot_damage_2.mp3"
    };

    @Override
    public void create() {

        Gdx.input.setCursorCatched(true);

        initializeSound();

        batch = new SpriteBatch();
        
        gameFont = new BitmapFont(load("font/ubuntu.fnt"));
        bigGameFont = new BitmapFont(load("font/ubuntu.fnt"));

        uiBG = extractSprites("sprites/default/ui_bg.png",32,32);
        uiControl = extractSprites("sprites/default/ui_control.png",32,32);
        hoverUIBG = extractSprites("sprites/default/ui_hover.png",32,32);


        loadingScreen = new LoadingScreen(this);
        gameScreen = new GameScreen(this);
        upgradeScreen = new UpgradeScreen(this);
        pauseScreen = new PauseScreen(this);


        setScreen(loadingScreen);

    }

    public void setGameScreen() {
        lockCursor(true);
        enableCursor = false;
        setScreen(gameScreen);
    }

    public void setPauseScreen() {
        lockCursor(false);
        enableCursor = true;
        setScreen(pauseScreen);
    }

    private void initializeSound() {
        for (String soundName : soundList) {
            soundBox.addSound(soundName, true);
        }
        soundBox.updateVolume();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
