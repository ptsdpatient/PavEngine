package com.pavengine.app.PavScreen;

import static com.pavengine.app.Debug.Draw.debugCube;
import static com.pavengine.app.Debug.Draw.debugRay;
import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Debug.Draw.debugRing;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.pavCamera;
import static com.pavengine.app.PavEngine.perspectiveViewport;
import static com.pavengine.app.PavEngine.sceneManager;

import static com.pavengine.app.Methods.loadModel;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.ThirdPersonCamera.camera;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.enableMapEditor;
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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PathFinder;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.DynamicObject;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavGameObject.GroundObject;
import com.pavengine.app.PavGameObject.KinematicObject;
import com.pavengine.app.PavGameObject.StaticObject;
import com.pavengine.app.PavGameObject.TargetObject;
import com.pavengine.app.PavLight.PavTorch;
import com.pavengine.app.PavPlayer.PavPlayer;
import com.pavengine.app.PavRay;
import com.pavengine.app.PavScript.Bullet;
import com.pavengine.app.PavScript.Enemies.Enemy;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;
import com.pavengine.app.PavUI.TextButton;

import net.mgsx.gltf.scene3d.scene.Scene;

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

    public static ShapeRenderer shapeRenderer;
    public static TextButton levelStatusButton;
    public PavLayout levelStatusLayout, levelStartTextLayout;
    public PavEngine game;
    public float levelStartTextTime = 5f;
    public ModelBatch batch;
    public TextButton levelStartText;
    public SpriteBatch spriteBatch;

    public BitmapFont font = new BitmapFont();


    public GameWorld (
        PavEngine game
    ) {

        this.game = game;
        this.spriteBatch = game.batch;
        shapeRenderer = new ShapeRenderer();
        batch = new ModelBatch();

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
            for(PavBounds box : obj.boxes)
                debugCube(box, obj.debugColor);
        }

        for (GameObject obj : staticObjects) {
            for(PavBounds box : obj.boxes)
                debugCube(box, obj.debugColor);

        }

        for (GameObject obj : groundObjects) {
            for(PavBounds box : obj.boxes)
                debugCube(box, obj.debugColor);
        }





//        for(PavLayout layout : mapEditingLayout) {
//            debugRectangle(layout.box,Color.BLUE);
//            for(PavWidget widget : layout.widgets) {
//                debugRectangle(widget.box,Color.RED);
//            }
//        }


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
//                for(Entrance b : obj.entrances) {
//                    debugCube(b.bounds);
//                }
//            }
//        }


//        for (Cell obj : pathFinder.grid) {
//            debugCell(obj);
//        }


        for (GameObject obj : kinematicObjects) {
            for(PavBounds box : obj.boxes){
                debugCube(box, obj.debugColor);
                if (obj.ringDetection) {
                    debugRing(box.rings);
                    debugRing(obj.footBox.rings);
                }
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
                levelStartTextLayout.draw(
                    spriteBatch,
                    overlayViewport.getWorldWidth(),
                    overlayViewport.getWorldHeight()
                );
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


        messageBoxLayout.draw(spriteBatch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());




        for (Bullet b : bullets) {
            b.update(delta);
            b.draw(spriteBatch, camera);
        }

//        cursor.draw(spriteBatch, delta);

        spriteBatch.end();

        for(PavLayout layout : gameWorldLayout) {
            for(PavWidget widget : layout.widgets) {
                debugRectangle(widget.box,Color.YELLOW);
            }
        }





        if(!enableMapEditor) {
            playerRay.update(delta);
        }
//        debugRay(playerRay);
    }


}
