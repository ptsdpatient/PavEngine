package com.pavengine.app;

import static com.pavengine.app.Methods.extractSprites;
import static com.pavengine.app.Methods.load;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.PavCamera.PavCamera.camera;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pavengine.app.PavCamera.FirstPersonCamera;
import com.pavengine.app.PavCamera.IsometricCamera;
import com.pavengine.app.PavCamera.PavCamera;
import com.pavengine.app.PavCamera.ThirdPersonCamera;
import com.pavengine.app.PavCamera.TopDownCamera;
import com.pavengine.app.PavScreen.GameScreen;
import com.pavengine.app.PavScreen.GameWorld;
import com.pavengine.app.PavScreen.LoadingScreen;
import com.pavengine.app.PavScreen.MapEditor;
import com.pavengine.app.PavScreen.PauseScreen;
import com.pavengine.app.PavScreen.UpgradeScreen;
import com.pavengine.app.PavSound.SoundBox;


public class PavEngine extends Game {
    public static CameraBehaviorType cameraBehavior;
    public static boolean dragAndDrop = true;
    public static boolean enableCursor = true;
    public static boolean enableMapEditor = true;
    public static boolean gamePause = false;
    public static Vector2 resolution = new Vector2(1280, 720);
    public static float sprayTime = 0f, sprayLimit = 5f, playerDamageTime = 0f, playerDamage = 30f, playerDamageRate = 0.3f, sprayCooldown = 0f, blastRadius = 5f;
    public static boolean levelStatus = false;
    public static float credits = 1000f, health = 100f;
    public static SoundBox soundBox = new SoundBox();

    public GameScreen gameScreen;
    public LoadingScreen loadingScreen;
    public UpgradeScreen upgradeScreen;
    public PauseScreen pauseScreen;
    public MapEditor mapEditor;

    public SpriteBatch batch;
    public static FitViewport overlayViewport, perspectiveViewport;
    public static PavCamera pavCamera;
    public static OrthographicCamera overlayCamera  = new OrthographicCamera();


    public static PavCursor cursor;

    public static BitmapFont
        gameFont,
        bigGameFont ;

    public static TextureRegion[]
        uiBG ,
        uiControl,
        hoverUIBG;

    public static boolean
        cel_shading = false,
        shadows = false;

    public String[] soundList = new String[]{
        "/winning/1.mp3", "/winning/2.mp3", "/winning/3.mp3", "/loss/1.mp3", "/loss/2.mp3", "/loss/3.mp3", "intro.mp3",
        "turret_1.mp3", "turret_2.wav", "rail_move.wav", "turret_reload.wav", "robot_damage.wav", "robot_damage_1.mp3", "robot_damage_2.mp3"
    };

    @Override
    public void create() {
        Gdx.input.setCursorCatched(true);

        if(!enableMapEditor) {
        } else {
            dragAndDrop = true;
        }

        initializeSound();

        cursor = new PavCursor(
            "sprites/default/cursor_sheet.png",
            275f
        );

        batch = new SpriteBatch();

        overlayCamera =new OrthographicCamera();
        overlayCamera.setToOrtho(false, resolution.x, resolution.y);

        overlayViewport = new FitViewport(resolution.x,resolution.y,overlayCamera);

        overlayViewport.apply();

        cameraBehavior = enableMapEditor ? CameraBehaviorType.TopDown : CameraBehaviorType.ThirdPerson;

        camera = new PerspectiveCamera(67, resolution.x, resolution.y);
        camera.near = 0.1f;
        camera.far = 1000f;
        perspectiveViewport = new FitViewport(resolution.x, resolution.y, camera);

        perspectiveViewport.apply();

        initializeCamera();

        gameFont = new BitmapFont(load("font/ubuntu.fnt"));
        bigGameFont = new BitmapFont(load("font/ubuntu.fnt"));

        uiBG = extractSprites("sprites/default/ui_bg.png",32,32);
        uiControl = extractSprites("sprites/default/ui_control.png",32,32);
        hoverUIBG = extractSprites("sprites/default/ui_hover.png",32,32);


        loadingScreen = new LoadingScreen(this);
        gameScreen = new GameScreen(this);
        upgradeScreen = new UpgradeScreen(this);
        pauseScreen = new PauseScreen(this);
        mapEditor = new MapEditor(this);




        if(enableMapEditor){
            setScreen(mapEditor);
        } else {
            setGameScreen();
        }

    }

    public void setGameScreen() {
        lockCursor(true);
        enableCursor = false;
        batch.setColor(1, 1, 1, 1);
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

    private void initializeCamera() {
        switch (cameraBehavior) {
            case FirstPerson:
                pavCamera = new FirstPersonCamera(67);

                break;

            case ThirdPerson:
                // Handle third-person camera logic
                pavCamera = new ThirdPersonCamera(67);
                break;

            case Isometric:
                pavCamera = new IsometricCamera(67);

                // Handle isometric camera logic
                break;

            case Orthographic:
                // Handle orthographic camera logic
                break;

            case TopDown:
                // Handle top-down camera logic
                pavCamera = new TopDownCamera(67, true);
                break;

            case SideScroller:
                // Handle side-scroller camera logic
                break;

            case FreeLook:
                // Handle free-look camera logic
                break;

            case Orbit:
                // Handle orbit camera logic
                break;

            case Cinematic:
                // Handle cinematic camera logic
                break;

            case Fixed:
                // Handle fixed camera logic
                break;

            case VR:
                // Handle VR camera logic
                break;

            case SplitScreen:
                // Handle split-screen camera logic
                break;

            default:
                // Handle unknown or unsupported behavior
                break;
        }

    }
}
