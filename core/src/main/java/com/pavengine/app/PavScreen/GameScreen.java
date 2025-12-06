package com.pavengine.app.PavScreen;

import static com.pavengine.app.Methods.getJson;
import static com.pavengine.app.Methods.loadModel;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.enableMapEditor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavScreen.GameWorld.dynamicObjects;
import static com.pavengine.app.PavScreen.GameWorld.groundObjects;
import static com.pavengine.app.PavScreen.GameWorld.kinematicObjects;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import static com.pavengine.app.PavUI.PavAnchor.BOTTOM_CENTER;
import static com.pavengine.app.PavUI.PavAnchor.CENTER;
import static com.pavengine.app.PavUI.PavFlex.ROW;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.DynamicObject;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavGameObject.GroundObject;
import com.pavengine.app.PavGameObject.KinematicObject;
import com.pavengine.app.PavGameObject.StaticObject;
import com.pavengine.app.PavGameObject.TargetObject;
import com.pavengine.app.PavParticle2D;
import com.pavengine.app.PavPlayer.Jump;
import com.pavengine.app.PavPlayer.Movement;
import com.pavengine.app.PavPlayer.PavPlayer;
import com.pavengine.app.PavRay;
import com.pavengine.app.PavScript.Bullet;
import com.pavengine.app.PavScript.Enemies.Enemy;
import com.pavengine.app.PavScript.LevelManager;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.TextBox;
import com.pavengine.app.PavUI.TextButton;

import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.shaders.PBRShaderProvider;

import java.util.ArrayList;

public class GameScreen extends PavScreen {

    public static GameObject selectedObject;
    public static PBRShaderProvider shaderProvider;
    public static GameWorld world;
    public static Array<Enemy> robots = new Array<>();
    public static Array<Bullet> bullets = new Array<>();
    public static PavParticle2D
        explodeEffect = new PavParticle2D("particles/explode/explode.p", "particles/explode"),
        damageSpark = new PavParticle2D("particles/spark/spark.p", "particles/spark"),
        bloodEffect = new PavParticle2D("particles/blood/blood.p", "particles/blood"),
        muzzleFlash = new PavParticle2D("particles/muzzle/muzzle.p", "particles/muzzle");

    public static PavRay playerRay;
    public static PavLayout
        messageBoxLayout,
        interactableLayout;
    public static Array<PavLayout>
        gameWorldLayout = new Array<>();
    public static Rectangle mapEditorPanel = new Rectangle(200, 0, 700, 300);
    public static TextBox messageBox = new TextBox("", gameFont[2], uiBG[2], ClickBehavior.NextMessage);
    public static LevelManager levelManager;
    public boolean intro = false;
    public PavEngine game;


    public GameScreen(PavEngine game) {
        super(game);

        this.game = game;
        this.batch = game.batch;

        if (enableMapEditor) {
            enableCursor = true;
        }

        lockCursor(!enableCursor);


        world = new GameWorld(
            game
        );



        interactableLayout  = new PavLayout(CENTER, ROW, 3, 64 * 3, 64);
        interactableLayout.addSprite(new TextButton("[ E ] Interact", gameFont[2], ClickBehavior.Nothing));

        messageBoxLayout = new PavLayout(BOTTOM_CENTER, ROW, 0, overlayCamera.viewportWidth, overlayCamera.viewportHeight / 3f, 12);
        messageBoxLayout.addSprite(messageBox);

//        addAndGet(gameWorldLayout, new PavLayout(CENTER, ROW, 3, 32, 32))
//            .addSprite(new Image("sprites/crosshair.png"));

//        addAndGet(gameWorldLayout, new PavLayout(BOTTOM_LEFT, ROW, 3, 192, 32, 3))
//            .addSprite(new HealthBar("Health", gameFont, ClickBehavior.Nothing, uiBG[5], uiBG[1]));
//
//        addAndGet(gameWorldLayout, new PavLayout(BOTTOM_RIGHT, ROW, 3, 192, 32, 3))
//            .addSprite(new SprayBar("", gameFont, ClickBehavior.Nothing, uiBG[6], uiBG[1]));
//
//        addAndGet(gameWorldLayout, new PavLayout(TOP_CENTER, ROW, 3, 192, 32, 25))
//            .addSprite(new ProgressBar("", gameFont, ClickBehavior.Nothing, uiBG[7], uiBG[1]));

//        world.addObject("gun2", "gun2", new Vector3(0, 0, 0), 0.2f, 10, 1, ObjectType.KINEMATIC, new String[]{"Shoot"});
//        world.getGameObject("gun2").objectBehaviorType = ObjectBehaviorType.AttachToCamera;
//        world.getGameObject("gun2").offset = new Vector3(1, -1, -3);

//        world.addObject("church", "church", new Vector3(25, -2.75f, 0), 10, 10, 0.4f, ObjectType.STATIC, new String[]{""});
//        world.getGameObject("church").rotation.setEulerAngles(-64,0,0);
//        world.getGameObject("church").setRoom(0.07f,
//            new ArrayList<>(Arrays.asList(
//
//                new EntranceBluprint() {{
//                    offset = new Vector3(-0.3f, 0.1f, 0.5f);
//                    size   = new Vector3(0.5f, 1, 2f);
//                    type   = Entrance.Type.DOOR;
//                    side   = EntranceBluprint.Side.LEFT;
//                }}
//
//            )
//        ));

//        for(int i = 0; i < 5 ; i++)
//            for(int j = 0; j < 5 ; j++)
//                world.addObject("ball", "ball", new Vector3(i*4, 5, j*4), 1.5f, 10, 0.4f, ObjectType.TARGET, new String[]{""});


//        world.addObject("door", "door", new Vector3(30, 0.3f, 0), 1.5f, 10, 0.4f, ObjectType.TARGET, new String[]{""});
//        world.getGameObject("door").setInteractAction(InteractType.DOOR);

//        world.addObject("tail", "tail", new Vector3(5, -1f, 10),2f, 10, 0.4f, ObjectType.TARGET, new String[]{"Wiggle","Sting"});
//        world.getGameObject("tail").setEnemy(new Vector3(0,2,-7),25,10,6f,5,true,new int[]{1});
//        world.getGameObject("tail").enemyAttackActionList.add(new EnemyAttackAction(1,0.7f,0.3f));
//        world.getGameObject("tail").forwardDirection.set(new Vector3(180,0,0));
//        world.getGameObject("tail").objectBehaviorType = ObjectBehaviorType.LookAtPlayer;
//        world.getGameObject("tail").padding.set(0,0.3f,-0.3f);
//        world.getGameObject("tail").playAnimation(0,true,false);


//        world.addObject("torch", "torch", new Vector3(5, -1f, 10), 2f, 10, 0.4f, ObjectType.KINEMATIC, new String[]{});
//        world.getGameObject("torch").attachToObject(world.getGameObject("player"),new Vector3(0, 0, 0));
//        world.getGameObject("torch").attachToCamera(new Vector3(0, 0, 0));
//        world.getGameObject("torch").forwardDirection.set(new Vector3(180, 0, 0));
//
//        torches.add(new PavTorch(world.getGameObject("torch"),new Vector3(2,2,-12), Color.WHITE,300,15,15));

//        world.getGameObject("pencil").inputBehaviorList.addAll(
//            Arrays.asList(
//                new PlayAnimationOnClick(0, 1, true, false),
//                new PlayAnimationOnClick(1, 2, false, false)
//            )
//        );
//
//      world.getGameObject("door").attachToObject(world.getGameObject("church"),new Vector3(-2, 1, 10.5f));
//      world.getGameObject("door").rotate(Direction.LEFT, 90);

//      gameWorldLayout.add(new PavLayout(TOP_LEFT,COLUMN,5,32,32));
//      for(int i : range(5)) {
//          gameWorldLayout.get(0).addSprite(new TextButton("text button : " + i,debugFont),new ExitGameButton());
//      }

//        print(overlayViewport.getWorldHeight() + " : "  +overlayViewport.getWorldHeight());

//        gameWorldLayout.add(new PavLayout(BOTTOM_CENTER,ROW,5,overlayViewport.getWorldWidth(),overlayViewport.getWorldHeight()/4f,10,uiBG[1]));
//        gameWorldLayout.get(gameWorldLayout.size()-1).addSprite(new TextBox("",debugFont, ClickBehavior.SkipTextBox));

//        world.addObject("ground", "ground", new Vector3(-10, -2, 0), 50, 0.4f, ObjectType.GROUND, new String[]{""});


        addObjects(
            new String[]{
//                "tree", "bush", "props", "lamp",
//                "ground"
            }
        );





        if(!enableMapEditor) {
            setPlayer();
            playerRay = new PavRay(
                world.getGameObject("player"),
                new Vector3(0, 1, 2), 2, 2, Color.CYAN
            );
        }


//        addObjects("gameobjects.json");

//        world.getGameObject("ground").rotation.setEulerAngles(0,0,15);
//        lasers.add(new RayCast(
//            world.getGameObject("gun2"),
//            new Vector3(1, 2, -3),
//            10f,
//            2f,
//            Color.BLUE
//        ));
//        setEnemyBlueprint();

        levelManager = new LevelManager("levels/levels.json", game);


    }

    @Override
    public void setInput() {
//        Gdx.input.setInputProcessor(gameWorldInput);
    }

    @Override
    public void debug() {

    }



    public static void addObjects(String[] fileNames) {
        for(String fileName : fileNames) {
            JsonValue root = getJson("list/" + fileName + ".json");

            if (root == null) return;

            for (JsonValue obj : root) {
                JsonValue
                    pos = obj.get("position"),
                    rot = obj.get("rotation"),
                    size = obj.get("size"),
                    room = obj.get("room"),
                    enemy = obj.get("enemy");
                String name = obj.getString("name");
                Scene scene = new Scene(loadModel("models/" + name + "/" + name + ".gltf").scene);

                switch (obj.getString("type")) {
                    case "STATIC": {
                        staticObjects.add(new StaticObject(
                                name,
                                scene,
                                new Vector3(pos.getFloat("x"), pos.getFloat("y"), pos.getFloat("z")),
                                new Quaternion(rot.getFloat("x"), rot.getFloat("y"), rot.getFloat("z"), rot.getFloat("w")),
                                new Vector3(size.getFloat("x"), size.getFloat("y"), size.getFloat("z"))
                            )
                        );
                    }
                    break;
                    case "GROUND": {
                        groundObjects.add(new GroundObject(
                                name,
                                scene,
                                new Vector3(pos.getFloat("x"), pos.getFloat("y"), pos.getFloat("z")),
                                new Quaternion(rot.getFloat("x"), rot.getFloat("y"), rot.getFloat("z"), rot.getFloat("w")),
                                new Vector3(size.getFloat("x"), size.getFloat("y"), size.getFloat("z"))
                            )
                        );
                    }
                    break;
                    case "TARGET": {
                        targetObjects.add(new TargetObject(
                                name,
                                scene,
                                new Vector3(pos.getFloat("x"), pos.getFloat("y"), pos.getFloat("z")),
                                new Quaternion(rot.getFloat("x"), rot.getFloat("y"), rot.getFloat("z"), rot.getFloat("w")),
                                new Vector3(size.getFloat("x"), size.getFloat("y"), size.getFloat("z"))
                            )
                        );
                        if (enemy.getBoolean("isEnemy")) {
                            JsonValue attackOffset = enemy.get("attackOffset");

                            JsonValue attackArray = obj.get("attackAnimation");
                            int[] attackAnimation = new int[attackArray.size];

                            for (int i = 0; i < attackArray.size; i++) {
                                attackAnimation[i] = attackArray.getInt(i);
                            }

                            targetObjects.get(targetObjects.size).setEnemy(
                                new Vector3(attackOffset.getFloat("x"), attackOffset.getFloat("y"), attackOffset.getFloat("z")),
                                enemy.getFloat("behaveRange"),
                                enemy.getFloat("attackRange"),
                                enemy.getFloat("fireRate"),
                                enemy.getFloat("damage"),
                                enemy.getBoolean("behaveIfCloseToPlayer"),
                                attackAnimation
                            );

                        }
                    }
                    break;
                    case "KINEMATIC": {
                        kinematicObjects.add(new KinematicObject(
                                name,
                                scene,
                                new Vector3(pos.getFloat("x"), pos.getFloat("y"), pos.getFloat("z")),
                                new Quaternion(rot.getFloat("x"), rot.getFloat("y"), rot.getFloat("z"), rot.getFloat("w")),
                                new Vector3(size.getFloat("x"), size.getFloat("y"), size.getFloat("z"))
                            )
                        );
                    }
                    break;
                    case "DYNAMIC": {
                        dynamicObjects.add(new DynamicObject(
                                name,
                                scene,
                                new Vector3(pos.getFloat("x"), pos.getFloat("y"), pos.getFloat("z")),
                                new Quaternion(rot.getFloat("x"), rot.getFloat("y"), rot.getFloat("z"), rot.getFloat("w")),
                                new Vector3(size.getFloat("x"), size.getFloat("y"), size.getFloat("z"))
                            )
                        );
                    }
                    break;
                }

                sceneManager.addScene(scene);
            }
        }
    }

    private void setPlayer() {
        world.addObject("player", "ball", new Vector3(0,20,0), 2f, 10, 1, ObjectType.KINEMATIC, new String[]{"Bounce", "Squish"});
        PavPlayer.player = world.getGameObject("player");
        PavPlayer.player.detectSlope = true;
        PavPlayer.player.setSlopeDetection();
        PavPlayer.player.setRing(2f,0);
        PavPlayer.playerBehavior.add(
            new Movement(),
            new Jump()
        );
    }


    @Override
    public void draw(float delta) {

    }



    @Override
    public void world(float delta) {
        world.render(delta);
    }



}
