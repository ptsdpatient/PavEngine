package com.pavengine.app;

import static com.pavengine.app.Methods.createSkybox;
import static com.pavengine.app.Methods.extractSprites;
import static com.pavengine.app.Methods.files;
import static com.pavengine.app.Methods.load;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pavengine.app.PavCamera.BoundsEditorCamera;
import com.pavengine.app.PavCamera.CinematicCamera;
import com.pavengine.app.PavCamera.FirstPersonCamera;
import com.pavengine.app.PavCamera.IsometricCamera;
import com.pavengine.app.PavCamera.MapEditorCamera;
import com.pavengine.app.PavCamera.PavCamera;
import com.pavengine.app.PavCamera.ThirdPersonCamera;
import com.pavengine.app.PavCamera.TopDownCamera;
import com.pavengine.app.PavLight.PavLight;
import com.pavengine.app.PavLight.PavLightProfile;
import com.pavengine.app.PavScreen.BoundsEditor;
import com.pavengine.app.PavScreen.CinematicEditor;
import com.pavengine.app.PavScreen.GameScreen;
import com.pavengine.app.PavScreen.LoadingScreen;
import com.pavengine.app.PavScreen.MapEditor;
import com.pavengine.app.PavScreen.PauseScreen;
import com.pavengine.app.PavScreen.UpgradeScreen;
import com.pavengine.app.PavSound.SoundBox;
import com.pavengine.app.PavUI.TextButton;

import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderConfig;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

public class PavEngine extends Game {
    public static Ray perspectiveTouchRay = new Ray();
    public static CameraBehaviorType cameraBehavior;

    public static boolean enableCursor = true;
    public static boolean enableMapEditor = true;
    public static boolean gamePause = false;
    public static Vector2 resolution = new Vector2(1280, 720);
    public static float sprayTime = 0f, sprayLimit = 5f, playerDamageTime = 0f, playerDamage = 30f, playerDamageRate = 0.3f, sprayCooldown = 0f, blastRadius = 5f;
    public static boolean levelStatus = false;
    public static float credits = 1000f, health = 100f;
    public static SoundBox soundBox = new SoundBox();

    public static Array<ReferenceOriginLine> centerReferenceOriginRays = new Array<>();

    public GameScreen gameScreen;
    public LoadingScreen loadingScreen;
    public UpgradeScreen upgradeScreen;
    public PauseScreen pauseScreen;
    public MapEditor mapEditor;
    public BoundsEditor boundsEditor;
    public CinematicEditor cinematicEditor;

    public SpriteBatch batch;
    public static FitViewport overlayViewport, perspectiveViewport;
    public static PavCamera pavCamera;
    public static OrthographicCamera overlayCamera  = new OrthographicCamera();

    public static AxisGizmo axisGizmo;
    public static PavCursor cursor;
    public static SceneManager sceneManager;
    public static PBRShaderConfig pbrConfig;
    public static DepthShaderProvider depthShader;

    public static Subtitle subtitle;

    public static BitmapFont[]
        gameFont = new BitmapFont[7];


    public PavLight pavLight;
    public static EditorSelectedObjectBehavior editorSelectedObjectBehavior = EditorSelectedObjectBehavior.FreeLook;
    public static TextButton editorSelectedObjectText;
    public static TextureRegion[]
        uiBG,
        uiControl,
        icons,
        hoverUIBG;

    public static boolean
        cel_shading = false,
        shadows = false;

    public static Array<ReferenceEditorLine> referenceEditorRays = new Array<>();

    public String[] soundList = new String[]{
        "/winning/1.mp3", "/winning/2.mp3", "/winning/3.mp3", "/loss/1.mp3", "/loss/2.mp3", "/loss/3.mp3", "intro.mp3",
        "turret_1.mp3", "turret_2.wav", "rail_move.wav", "turret_reload.wav", "robot_damage.wav", "robot_damage_1.mp3", "robot_damage_2.mp3"
    };

    public PavEngine() {

    }

    @Override
    public void create() {

        Gdx.input.setCursorCatched(true);

        setUpMapEditorLines(30,10, Color.DARK_GRAY);

        if(!enableMapEditor) {

        } else {
//            dragAndDrop = true;
        }

        initializeSound();

        cursor = new PavCursor(
            "sprites/default/cursor_sheet.png",
            125f
        );

        pbrConfig = PBRShaderProvider.createDefaultConfig();
        pbrConfig.numSpotLights = 2;
        pbrConfig.numBones = 32;

        depthShader = new DepthShaderProvider();
        depthShader.config.numSpotLights = 2;
        depthShader.config.numBones = 32;

        if(cel_shading) {
            pbrConfig.vertexShader = files("shaders/cel/vs.glsl").readString();
            pbrConfig.fragmentShader = files("shaders/cel/fs.glsl").readString();
        }

        batch = new SpriteBatch();

        overlayCamera =new OrthographicCamera();
        overlayCamera.setToOrtho(false, resolution.x, resolution.y);

        overlayViewport = new FitViewport(resolution.x,resolution.y, overlayCamera);

        overlayViewport.apply();

        axisGizmo = new AxisGizmo(overlayCamera);

        cameraBehavior = CameraBehaviorType.BoundsEditor;

        camera = new PerspectiveCamera(67, resolution.x/2, resolution.y/2);
        camera.near = 0.1f;
        camera.far = 1000f;
        perspectiveViewport = new FitViewport(resolution.x/2, resolution.y/2, camera);

        perspectiveViewport.apply();
//        print(perspectiveViewport.getWorldWidth() + " , " + perspectiveViewport.getWorldHeight());

//        print(perspectiveViewport.getWorldWidth() + " , " + perspectiveViewport.getWorldHeight());

        initializeCamera();

        sceneManager = new SceneManager(
            new PBRShaderProvider(pbrConfig),
            depthShader
        );

        sceneManager.setCamera(camera);
        pavLight = new PavLight(sceneManager.environment, PavLightProfile.DAY);

        sceneManager.setSkyBox(createSkybox("skybox/default/sky.png"));
        for(int i = 1; i < 7 ; i++) {
            gameFont[i-1] = new BitmapFont(load("font/default_" + i + ".fnt"));
        }

        uiBG = extractSprites("sprites/default/ui_bg.png",32,32);
        uiControl = extractSprites("sprites/default/ui_control.png",32,32);
        hoverUIBG = extractSprites("sprites/default/ui_hover.png",32,32);
        icons = extractSprites("sprites/default/icons.png",32,32);

        subtitle = new Subtitle(gameFont[2],resolution.x,resolution.y);

        loadingScreen = new LoadingScreen(this);
        gameScreen = new GameScreen(this);
        upgradeScreen = new UpgradeScreen(this);
        pauseScreen = new PauseScreen(this);
        mapEditor = new MapEditor(this);
        boundsEditor = new BoundsEditor(this);
        cinematicEditor = new CinematicEditor(this);

        setScreen(boundsEditor);

    }

    private void setUpMapEditorLines(int size,int step,Color gridColor) {

//        perspectiveAxisGizmo = new AxisGizmo3D();


//        centerReferenceOriginRays.add(new ReferenceOriginLine(new Vector3(0,5,0), Color.RED));
//        centerReferenceOriginRays.add(new ReferenceOriginLine(new Vector3(5,0,0), Color.BLUE));
//        centerReferenceOriginRays.add(new ReferenceOriginLine(new Vector3(0,0,5), Color.GREEN));

        for (int i = -size; i <= size; i++) {
            referenceEditorRays.add(new ReferenceEditorLine(
                new Vector3(-size * step, 0, i * step),
                new Vector3(size * step, 0, i * step),
                i==0?Color.RED:gridColor
            ));

            referenceEditorRays.add(new ReferenceEditorLine(
                new Vector3(i * step, 0, -size * step),
                new Vector3(i * step, 0, size * step),
                i==0?Color.GREEN:gridColor
            ));
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
            case Cinematic:
                pavCamera = new CinematicCamera(67);
                break;

            case MapEditorCamera:
                pavCamera = new MapEditorCamera(67);
                break;

            case BoundsEditor:
                pavCamera = new BoundsEditorCamera(67);
                break;

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
