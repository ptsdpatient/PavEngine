package com.pavengine.app.PavScreen;

import static com.pavengine.app.Debug.Draw.debugCube;
import static com.pavengine.app.Debug.Draw.debugRay;
import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Debug.Draw.debugRing;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.pavCamera;
import static com.pavengine.app.PavEngine.perspectiveViewport;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.elevationStepper;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.mapEditingLayout;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.roomCheckbox;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.rotationSteppers;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.scaleStepper;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.selectedObjectType;
import static com.pavengine.app.Methods.loadModel;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.ThirdPersonCamera.camera;
import static com.pavengine.app.PavEngine.credits;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.enableMapEditor;
import static com.pavengine.app.PavEngine.gamePause;
import static com.pavengine.app.PavEngine.health;
import static com.pavengine.app.PavEngine.hoverUIBG;
import static com.pavengine.app.PavEngine.levelStatus;
import static com.pavengine.app.PavEngine.soundBox;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavEngine.uiControl;
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
import static com.pavengine.app.PavUI.PavAnchor.CENTER_LEFT;
import static com.pavengine.app.PavUI.PavAnchor.TOP_RIGHT;
import static com.pavengine.app.PavUI.PavFlex.COLUMN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import com.pavengine.app.PavUI.Checkbox;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.Dropdown;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;
import com.pavengine.app.PavUI.Stepper;
import com.pavengine.app.PavUI.TextButton;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

import java.util.ArrayList;
import java.util.Objects;

public class GameWorld {


    public static Array<PavTorch> torches = new Array<>();
    public static Array<GameObject>
        staticObjects = new Array<>(),
        dynamicObjects = new Array<>(),
        kinematicObjects = new Array<>(),
        targetObjects = new Array<>(),
        groundObjects = new Array<>();

    public static Array<PavRay> lasers = new Array<>();
    public static PathFinder pathFinder = new PathFinder();

    public static SceneManager sceneManager;
    public static ShapeRenderer shapeRenderer;
    public static TextButton levelStatusButton;
    public PavLayout levelStatusLayout, levelStartTextLayout;
    public PavEngine game;
    public float levelStartTextTime = 5f;
    public ModelBatch batch;
    public TextButton levelStartText;
    public SpriteBatch spriteBatch;
    public PavLight pavLight;

    public BitmapFont font = new BitmapFont();
    public PavSkyBox skyBox;

    Vector2 resolution;


    public GameWorld (
        PavEngine game,
        Vector2 resolution,
        PavLightProfile lightProfile,
        PBRShaderProvider shaderProvider,
        DepthShaderProvider depthShaderProvider
    ) {
        this.resolution = resolution;
        this.game = game;
        this.spriteBatch = game.batch;
        shapeRenderer = new ShapeRenderer();
        batch = new ModelBatch();
        sceneManager = new SceneManager(shaderProvider, depthShaderProvider);
        pavLight = new PavLight(sceneManager.environment, lightProfile);



        sceneManager.setCamera(camera);

//        overlayCamera.position.set(camera.position.cpy());

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

//        levelStatusLayout = new PavLayout(PavAnchor.CENTER, PavFlex.COLUMN, 5, 720, 64);
//        levelStatusButton = new TextButton("You Won! (Click to Continue)", gameFont, ClickBehavior.Nothing);
//        levelStatusLayout.addSprite(levelStatusButton);


//        levelStartTextLayout = new PavLayout(PavAnchor.TOP_CENTER, PavFlex.COLUMN, 5, 720, 64);
//        levelStartText = new TextButton("Level 1", bigGameFont, ClickBehavior.Nothing);
//        levelStartTextLayout.addSprite(levelStartText);

//        creditShow = new TextButton(String.valueOf(PavEngine.credits), gameFont, ClickBehavior.Nothing);
//        gameWorldLayout.add(new PavLayout(PavAnchor.TOP_RIGHT, PavFlex.COLUMN, 0, 192, 16, 8));
//        gameWorldLayout.peek().addSprite(creditShow);

    }




    public GameObject getGameObject(String name) {
        for (GameObject obj : groundObjects) {
            if (Objects.equals(obj.name, name)) {
                return obj;
            }
        }
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

        if(!enableMapEditor) {
//            creditShow.text = ((int) credits) + " Credits";
        }

//        if (!gamePause) {
//            levelManager.update(delta);
//        }

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
        for (GameObject obj : targetObjects) {
            debugCube(obj.box, obj.debugColor);
        }

        for (GameObject obj : staticObjects) {
            debugCube(obj.box, obj.debugColor);

        }

        for (GameObject obj : groundObjects) {
            debugCube(obj.box,obj.debugColor);
        }


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


        for (GameObject obj : kinematicObjects) {
            debugCube(obj.box);
            if (obj.ringDetection) {
                debugRing(obj.box.rings);
                debugRing(obj.footBox.rings);
            }
            if(obj.detectSlope) {
//                for(SlopeRay ray : obj.slopeRays){
//                    debugRay(ray);
//                }
//                print(obj.slopeNormal);
//                debugLine(
//                    obj.pos.cpy(),
//                    obj.pos.cpy().add(obj.slopeNormal.cpy().scl(10)
//                    )
//                );

            }
        }

        shapeRenderer.setColor(Color.YELLOW);

        for (PavRay ray : lasers) {
            debugRay(ray);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_DEPTH_TEST);


        pavCamera.update(delta);
        camera.update();
        overlayCamera.update();

        spriteBatch.setProjectionMatrix(overlayCamera.combined);
        spriteBatch.begin();

        if(!enableMapEditor) {
            if (levelStartTextTime >= 0f && !gamePause) {
                levelStartTextTime -= delta;
                levelStartTextLayout.draw(spriteBatch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());
            }
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

        for(PavLayout layout : gameWorldLayout) {
            for(PavWidget widget : layout.widgets) {
                debugRectangle(widget.box,Color.YELLOW);
            }
        }

        if(enableMapEditor) {
            for(PavLayout layout : mapEditingLayout) {
                for(PavWidget widget : layout.widgets) {
                    if(widget.isHovered) debugRectangle(widget.box,Color.GREEN);
                }
            }
        }



        if(!enableMapEditor) {
            playerRay.update(delta);
        }
//        debugRay(playerRay);
    }

    public static class MapEditor {
        public static Array<GameObject> staticMapObjects;
        public static ArrayList<String> objectList;
        public static ArrayList<PavLayout> mapEditingLayout = new ArrayList<>();
        public static Stepper scaleStepper, elevationStepper;
        public static Checkbox roomCheckbox;
        public static Dropdown selectedObjectType;
        public static ArrayList<Stepper> rotationSteppers = new ArrayList<>();
        public static PavWidget exportModelInfo;
        public Vector3[] rotationOffset = new Vector3[]{new Vector3(1, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1)};
        public String[] rotationNames = new String[]{"Rotation Yaw", "Rotation Roll", "Rotation Pitch"};

        public MapEditor(BitmapFont font) {
            staticMapObjects = new Array<>();
            objectList = new ArrayList<>();

            mapEditingLayout.add(new PavLayout(CENTER_LEFT, COLUMN, 5, 192, 128, 5));
            for (String model : listModels("assets/models/"))
                mapEditingLayout.get(0).addSprite(new TextButton(model, font, hoverUIBG[1], uiBG[1], ClickBehavior.AddStaticObjectToMapEditor));


            scaleStepper = new Stepper(192 + 32, 140 - 20, new Vector3(0.005f, 0.005f, 0.005f), ClickBehavior.StepperScale, "Scale", font, uiControl[0], uiControl[1]);
            elevationStepper = new Stepper(192 * 2 + 32, 140 - 20, new Vector3(0f, 0.05f, 0f), ClickBehavior.StepperElevation, "Elevation", font, uiControl[0], uiControl[1]);
            roomCheckbox = new Checkbox(192 * 3 + 32, 140 - 20, false, ClickBehavior.CheckboxRoom, "Room", font, uiControl[4], uiControl[5]);
            selectedObjectType = new Dropdown(192 + 32, 200, new String[]{"StaticObject", "TargetObject", "GroundObject", "KinematicObject"}, 1, font);
            mapEditingLayout.add(new PavLayout(TOP_RIGHT, COLUMN, 5, 192, 48, 5));
            mapEditingLayout.get(1).addSprite(new TextButton("Export", font, hoverUIBG[3], uiBG[2], ClickBehavior.ExportModelInfo));


            int i = 0;
            for (Vector3 offset : rotationOffset) {
                rotationSteppers.add(new Stepper(192 * (i + 1) + 32, 50 - 20, offset, ClickBehavior.StepperRotation, rotationNames[i], font, uiControl[0], uiControl[1]));
                i++;
            }

        }

        public ArrayList<String> listModels(String path) {
            ArrayList<String> folders = new ArrayList<>();

            FileHandle dir = Gdx.files.internal(path);

            if (dir.exists()) {
                for (FileHandle file : dir.list()) {
                    if (file.isDirectory()) {
                        folders.add(file.name());
                    }
                }
            }
            return folders;
        }

    }
}
