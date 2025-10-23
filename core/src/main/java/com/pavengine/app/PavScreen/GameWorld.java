package com.pavengine.app.PavScreen;

import static com.pavengine.app.Debug.Draw.debugCube;
import static com.pavengine.app.MapEditor.MapEditor.elevationStepper;
import static com.pavengine.app.MapEditor.MapEditor.mapEditingLayout;
import static com.pavengine.app.MapEditor.MapEditor.roomCheckbox;
import static com.pavengine.app.MapEditor.MapEditor.rotationSteppers;
import static com.pavengine.app.MapEditor.MapEditor.scaleStepper;
import static com.pavengine.app.MapEditor.MapEditor.selectedObjectType;
import static com.pavengine.app.Methods.loadModel;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.ThirdPersonCamera.camera;
import static com.pavengine.app.PavEngine.bigGameFont;
import static com.pavengine.app.PavEngine.cel_shading;
import static com.pavengine.app.PavEngine.credits;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.enableMapEditor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.gamePause;
import static com.pavengine.app.PavEngine.health;
import static com.pavengine.app.PavEngine.levelStatus;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavScreen.GameScreen.bloodEffect;
import static com.pavengine.app.PavScreen.GameScreen.bullets;
import static com.pavengine.app.PavScreen.GameScreen.damageSpark;

import static com.pavengine.app.PavScreen.GameScreen.explodeEffect;

import static com.pavengine.app.PavScreen.GameScreen.gameWorldLayout;
import static com.pavengine.app.PavScreen.GameScreen.levelManager;
import static com.pavengine.app.PavScreen.GameScreen.messageBoxLayout;
import static com.pavengine.app.PavScreen.GameScreen.muzzleFlash;
import static com.pavengine.app.PavScreen.GameScreen.playerRay;
import static com.pavengine.app.PavScreen.GameScreen.robots;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.PavScreen.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.pavengine.app.CameraBehaviorType;
import com.pavengine.app.Cell;
import com.pavengine.app.GameSprite;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PathFinder;
import com.pavengine.app.PavCamera.FirstPersonCamera;
import com.pavengine.app.PavCamera.IsometricCamera;
import com.pavengine.app.PavCamera.PavCamera;
import com.pavengine.app.PavCamera.ThirdPersonCamera;
import com.pavengine.app.PavCamera.TopDownCamera;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.DynamicObject;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavGameObject.GroundObject;
import com.pavengine.app.PavGameObject.KinematicObject;
import com.pavengine.app.PavGameObject.StaticObject;
import com.pavengine.app.PavGameObject.TargetObject;
import com.pavengine.app.PavLight.PavLight;
import com.pavengine.app.PavLight.PavLightProfile;
import com.pavengine.app.PavLight.PavTorch;
import com.pavengine.app.PavPlayer.PavPlayer;
import com.pavengine.app.PavRay;
import com.pavengine.app.PavScript.Bullet;
import com.pavengine.app.PavScript.Enemies.Enemy;
import com.pavengine.app.PavSkyBox;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.PavAnchor;
import com.pavengine.app.PavUI.PavFlex;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.Stepper;
import com.pavengine.app.PavUI.TextButton;

import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

import java.util.Objects;

public class GameWorld {

    public static CameraBehaviorType cameraBehavior;
    public static Array<PavTorch> torches = new Array<>();
    public static Array<GameObject>
        staticObjects = new Array<>(),
        dynamicObjects = new Array<>(),
        kinematicObjects = new Array<>(),
        targetObjects = new Array<>(),
        groundObjects = new Array<>();
    public static Array<GameSprite> sprites = new Array<>();
    public static Array<PavRay> lasers = new Array<>();
    public static PathFinder pathFinder = new PathFinder();
    public static PavCamera pavCamera;
    public static SceneManager sceneManager;
    public static OrthographicCamera overlayCamera;
    public static ShapeRenderer shapeRenderer;
    public static FitViewport overlayViewport, perspectiveViewport;
    public static TextButton levelStatusButton;
    public PavLayout levelStatusLayout, levelStartTextLayout;
    public PavEngine game;
    public float levelStartTextTime = 5f;
    public ModelBatch batch;
    public TextureRegion tex = new TextureRegion();
    public TextButton levelStartText;
    public SpriteBatch spriteBatch;
    public PavLight pavLight;
    public DirectionalLightEx light;
    public Model cubeModel;
    public BitmapFont font = new BitmapFont();
    public float deltaX = 0, deltaY = 0, sensitivity = 0.1f, playerSpeed = 0.05f;
    public Vector3 right = new Vector3(), leftMove = new Vector3(), rightMove = new Vector3(), forward = new Vector3(), backward = new Vector3();
    public PavSkyBox skyBox;
    Vector3 flatForward = new Vector3(), flatRight = new Vector3(), cameraOffset = new Vector3(5, 0, 0);
    float speed = 5f;
    Vector2 resolution;
    private TextButton creditShow;

    public GameWorld(PavEngine game, CameraBehaviorType cameraBehavior, Vector2 resolution, PavLightProfile lightProfile, PBRShaderProvider shaderProvider, DepthShaderProvider depthShaderProvider) {
        GameWorld.cameraBehavior = cameraBehavior;
        this.resolution = resolution;
        this.game = game;
        this.spriteBatch = game.batch;
        shapeRenderer = new ShapeRenderer();
        batch = new ModelBatch();
        sceneManager = new SceneManager(shaderProvider, depthShaderProvider);
        pavLight = new PavLight(sceneManager.environment, lightProfile);
//        pavLight.addPointLight(Color.CYAN,new Vector3(5,1,2),500);


        overlayCamera = new OrthographicCamera();
        overlayViewport = new FitViewport(resolution.x, resolution.y, overlayCamera);
        overlayCamera.setToOrtho(false, resolution.x, resolution.y);

        camera = new PerspectiveCamera(67, resolution.x, resolution.y);
        camera.near = 0.1f;
        camera.far = 1000f;
        perspectiveViewport = new FitViewport(resolution.x, resolution.y, camera);

        overlayViewport.apply();
        perspectiveViewport.apply();

        sceneManager.setCamera(camera);


//        overlayCamera.position.set(camera.position.cpy());
        initializeCamera();
//        for (int k = 0;k<=7;k++) {
//        for (int i = 0; i <= 7; i++) {
//            for (int j = 0; j <= 7; j++) {
//                pathFinder.addCell(new Vector3(0, i, j));
//            }
//        }
//        }

        for (Cell cell : pathFinder.grid) {
//            addObject("cell" ,"cube",cell.position,20,0,ObjectType.TARGET,new String[]{"scale"});
        }

        skyBox = new PavSkyBox("sky", new Vector3(0, 0, 0), 10);

        levelStatusLayout = new PavLayout(PavAnchor.CENTER, PavFlex.COLUMN, 5, 720, 64);
        levelStatusButton = new TextButton("You Won! (Click to Continue)", gameFont, ClickBehavior.Nothing);
        levelStatusLayout.addSprite(levelStatusButton);


        levelStartTextLayout = new PavLayout(PavAnchor.TOP_CENTER, PavFlex.COLUMN, 5, 720, 64);
        levelStartText = new TextButton("Level 1", bigGameFont, ClickBehavior.Nothing);
        levelStartTextLayout.addSprite(levelStartText);

        creditShow = new TextButton(String.valueOf(PavEngine.credits), gameFont, ClickBehavior.Nothing);
        gameWorldLayout.add(new PavLayout(PavAnchor.TOP_RIGHT, PavFlex.COLUMN, 0, 192, 16, 8));
        gameWorldLayout.peek().addSprite(creditShow);

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


    public GameObject getGameObject(String name) {
        for (GameObject obj : staticObjects) {
            if (Objects.equals(obj.name, name)) {
                return obj;
            }
        }
        for (GameObject obj : targetObjects) {
            if (Objects.equals(obj.name, name)) {
                return obj;
            }
        }
        for (GameObject obj : dynamicObjects) {
            if (Objects.equals(obj.name, name)) {
                return obj;
            }
        }
        for (GameObject obj : kinematicObjects) {
            if (Objects.equals(obj.name, name)) {
                return obj;
            }
        }
        return null;
    }

    public void addObject(String name, String model, Vector3 position, float mass, float bounciness, ObjectType type, String[] animationNames) {
        switch (type) {
            case TARGET: {
                targetObjects.add(new TargetObject(name, position, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(targetObjects.get(targetObjects.size - 1).scene);
            }
            break;
            case STATIC: {
                staticObjects.add(new StaticObject(name, position, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(staticObjects.get(staticObjects.size - 1).scene);
            }
            break;
            case DYNAMIC: {
                dynamicObjects.add(new DynamicObject(name, position, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(dynamicObjects.get(dynamicObjects.size - 1).scene);
            }
            break;
            case KINEMATIC: {
                kinematicObjects.add(new KinematicObject(name, position, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(kinematicObjects.get(kinematicObjects.size - 1).scene);
            }
            break;
            case GROUND: {
                groundObjects.add(new GroundObject(name, position, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(groundObjects.get(groundObjects.size - 1).scene);
            }
            break;
        }
    }

    public void addObject(String name, String model, Vector3 position, float scale, float mass, float bounciness, ObjectType type, String[] animationNames) {
        switch (type) {
            case TARGET: {
                targetObjects.add(new TargetObject(name, position, scale, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(targetObjects.get(targetObjects.size - 1).scene);
            }
            break;
            case STATIC: {
                staticObjects.add(new StaticObject(name, position, scale, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(staticObjects.get(staticObjects.size - 1).scene);
            }
            break;
            case DYNAMIC: {
                dynamicObjects.add(new DynamicObject(name, position, scale, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(dynamicObjects.get(dynamicObjects.size - 1).scene);
            }
            break;
            case KINEMATIC: {
                kinematicObjects.add(new KinematicObject(name, position, scale, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(kinematicObjects.get(kinematicObjects.size - 1).scene);
            }
            break;
            case GROUND: {
                groundObjects.add(new GroundObject(name, position, scale, new Scene(loadModel("models/" + model + "/" + model + ".gltf").scene), mass, bounciness, type, animationNames));
                sceneManager.addScene(groundObjects.get(groundObjects.size - 1).scene);
            }
            break;
        }
    }


    public void resize(int width, int height) {
        perspectiveViewport.update(width, height, true);
        overlayViewport.update(width, height, true);

        sceneManager.updateViewport(width, height);

        camera.update();
        overlayCamera.update();

        perspectiveViewport.apply();
        overlayViewport.apply();
    }


    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_FRONT);
        Gdx.gl.glCullFace(GL20.GL_BACK);
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setPauseScreen();
        }

        creditShow.text = ((int) credits) + " Credits";

        if (!gamePause) {
            levelManager.update(delta);
        }

        if (health <= 0) {

            switch (MathUtils.random(1, 3)) {
                case 1: {
                    soundBox.playSound("/loss/1.mp3");
                }
                break;
                case 2: {
                    soundBox.playSound("/loss/2.mp3");
                }
                break;
                case 3: {
                    soundBox.playSound("/loss/3.mp3");
                }
                break;
            }

            levelStatus = true;
            gamePause = true;
            lockCursor(false);

            levelStatusButton.text = "You lost. Restart?";
        }

        for (GameObject obj : dynamicObjects) {
            obj.update(delta);
        }

        for (GameObject obj : staticObjects) {
            obj.update(delta);
        }

        for (GameObject obj : targetObjects) {
            obj.update(delta);
        }

        for (GameObject obj : kinematicObjects) {
            obj.update(delta);
            if (obj.detectSlope) obj.slopeDetection();
        }

        for (Enemy enemy : robots) {
            enemy.update(delta);
        }

        for (PavRay laser : lasers) {
            laser.update(delta);
        }

        for (PavTorch torch : torches) {
            torch.update(delta);
        }


        sceneManager.update(delta);
        sceneManager.render();

//        playerControl(delta);

        PavPlayer.update(delta);

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glLineWidth(3f);

//        Debug Target object
//        for (GameObject obj : targetObjects) {
//            debugCube(obj.box, obj.debugColor);
//        }

//        for (GameObject obj : groundObjects) {
//            debugCube(obj.box,obj.debugColor);
//        }


//        for(PavLayout layout : mapEditingLayout) {
//            debugRectangle(layout.box,Color.BLUE);
//            for(PavWidget widget : layout.widgets) {
//                debugRectangle(widget.box,Color.RED);
//            }
//        }

        for (Stepper stepper : rotationSteppers) {
//            debugRectangle(stepper.box,Color.BLUE);

        }
//        debugRectangle(roomCheckbox.box,Color.BLUE);
//        for (GameObject obj : staticObjects) {
//            if (!obj.isRoom) debugCube(obj.box,obj.debugColor);
//            if (obj.isRoom) {
//                for (Entrance e : obj.entrances) {
//                    debugCube(e.bounds,Color.BLUE);
//                }
//                for (PavBounds b : obj.walls) {
//                    debugCube(b);
//                }
////                for(Entrance b : obj.entrances) {
////                    debugCube(b.bounds);
////                }
//            }
//        }


//        for (Cell obj : pathFinder.grid) {
//            debugCell(obj);
//        }


//        for (GameObject obj : kinematicObjects) {
//            debugCube(obj.box);
//            if (obj.ringDetection) {
//                debugRing(obj.box.rings);
//            }
//        }

        shapeRenderer.setColor(Color.YELLOW);

//        for (PavRay ray : lasers) {
//            debugRay(ray);
//        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);


        pavCamera.update(delta);
        camera.update();
        overlayCamera.update();

        spriteBatch.setProjectionMatrix(overlayCamera.combined);
        spriteBatch.begin();

        if (levelStartTextTime >= 0f && !gamePause) {
            levelStartTextTime -= delta;
            levelStartTextLayout.draw(spriteBatch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());
        }


        if (levelStatus) {

            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                if (health <= 0) {
                    levelManager.restart();
                } else {

                    lockCursor(false);
                    enableCursor = true;
                    game.setScreen(game.upgradeScreen);
                }

                health = 100;

                levelStartTextTime = 5f;
                levelStartText.text = "Level " + levelManager.getCurrentLevel();


                levelStatus = false;
            }

            levelStatusLayout.draw(spriteBatch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());
        }

        muzzleFlash.update(spriteBatch, delta);
        bloodEffect.update(spriteBatch, delta);

        damageSpark.update(spriteBatch, delta);
        explodeEffect.update(spriteBatch, delta);

        if (!gamePause) {
            for (PavLayout layout : gameWorldLayout) {

                layout.draw(spriteBatch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());

            }
        }

        for (PavLayout layout : mapEditingLayout) {
            layout.draw(spriteBatch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());
        }
        messageBoxLayout.draw(spriteBatch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());


        if (selectedObject != null && enableMapEditor) {
            scaleStepper.render(spriteBatch);
            elevationStepper.render(spriteBatch);
            roomCheckbox.render(spriteBatch);
            selectedObjectType.render(spriteBatch);
            for (Stepper stepper : rotationSteppers) {
                stepper.render(spriteBatch);
            }
        }

        for (Bullet b : bullets) {
            b.update(delta);
            b.draw(spriteBatch, camera);
        }

        cursor.draw(spriteBatch, delta);

        spriteBatch.end();

//        for(PavLayout layout : gameWorldLayout) {
//            for(PavWidget widget : layout.widgets) {
//                debugRectangle(widget.box,Color.YELLOW);
//            }
//        }
//        for(Lane lane : lanes) {
//            debugLine(lane.start,lane.end);
//        }
        playerRay.update(delta);
//        debugRay(playerRay);
    }

}
