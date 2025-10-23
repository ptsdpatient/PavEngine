package com.pavengine.app;

import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavScreen.GameWorld.dynamicObjects;
import static com.pavengine.app.PavScreen.GameWorld.groundObjects;
import static com.pavengine.app.PavScreen.GameWorld.kinematicObjects;
import static com.pavengine.app.PavScreen.GameWorld.sceneManager;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;
import static com.pavengine.app.PavScreen.PavScreen.cursor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.pavengine.app.PavBounds.Entrance;
import com.pavengine.app.PavBounds.EntranceBluprint;
import com.pavengine.app.PavGameObject.DynamicObject;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavGameObject.GroundObject;
import com.pavengine.app.PavGameObject.KinematicObject;
import com.pavengine.app.PavGameObject.StaticObject;
import com.pavengine.app.PavGameObject.TargetObject;
import com.pavengine.app.PavScript.Interactable.Interactable;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

import java.util.ArrayList;

public class Methods {


    public static void print(String value) {
        Gdx.app.log("Game", value);
    }

    public static void print(float value) {
        Gdx.app.log("Game", String.valueOf(value));
    }

    public static void print(Vector3 value) {
        Gdx.app.log("Game", value + "");
    }

    public static void print(String tag, String value) {
        Gdx.app.log(tag, value);
    }


    public static FileHandle files(String value) {
        return Gdx.files.internal(value);
    }

    public static SceneAsset loadModel(String value) {
        return new GLTFLoader().load(files(value));
    }

    public static JsonValue getJson(String fileName) {
        return new JsonReader().parse(Gdx.files.internal(fileName));
    }

    public static FileHandle load(String value) {
        return Gdx.files.internal(value);
    }

    public static <T> T addAndGet(Array<T> array, T element) {
        array.add(element);
        return element;
    }

    public static TextureRegion[] extractSprites(String name, int width, int height) {
        TextureRegion sheet = new TextureRegion(new Texture(load(name)));
        return sheet.split(width, height)[0];
    }

    public static void lockCursor(boolean lock) {
        enableCursor = !lock;
        cursor.setCursor(lock ? 0 : 1);
    }

    public static boolean isKeyPressed(String keyName) {
        Integer keyCode = getKeyCode(keyName);
        return keyCode != null && Gdx.input.isKeyPressed(keyCode);
    }

    public static boolean isKeyJustPressed(String keyName) {
        Integer keyCode = getKeyCode(keyName);
        return keyCode != null && Gdx.input.isKeyJustPressed(keyCode);
    }

    public static boolean isButtonPressed(String buttonName) {
        Integer button = getButtonCode(buttonName);
        return button != null && Gdx.input.isButtonPressed(button);
    }

    public static boolean isButtonJustPressed(String buttonName) {
        Integer button = getButtonCode(buttonName);
        return button != null && Gdx.input.isButtonJustPressed(button);
    }

    // --- helper methods ---
    private static Integer getKeyCode(String keyName) {
        if (keyName == null) return null;
        switch (keyName.toUpperCase()) {
            case "W":
                return Input.Keys.W;
            case "A":
                return Input.Keys.A;
            case "S":
                return Input.Keys.S;
            case "D":
                return Input.Keys.D;
            case "SPACE":
                return Input.Keys.SPACE;
            case "ENTER":
                return Input.Keys.ENTER;
            case "ESCAPE":
                return Input.Keys.ESCAPE;
            // add all other keys you use
            default:
                return null;
        }
    }

    private static Integer getButtonCode(String buttonName) {
        if (buttonName == null) return null;
        switch (buttonName.toUpperCase()) {
            case "LEFT":
                return Input.Buttons.LEFT;
            case "RIGHT":
                return Input.Buttons.RIGHT;
            case "MIDDLE":
                return Input.Buttons.MIDDLE;
            // add other buttons if needed
            default:
                return null;
        }
    }


    public static void jsonMapInteractible(ArrayList<Interactable> list, String json) {
        ArrayList<GameObject> objects = getObjectList(json);
        int size = Math.min(list.size(), objects.size());

        for (int i = 0; i < size; i++) {
            list.get(i).object = objects.get(i);
        }
    }

    public static ArrayList<GameObject> getObjectList(String fileName) {
        ArrayList<GameObject> objectList = new ArrayList<>();

        JsonValue root = getJson("list/" + fileName);

        if (root == null) return objectList;
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

                    if (room.getBoolean("isRoom")) {
                        ArrayList<EntranceBluprint> entrances = new ArrayList<>();
                        for (JsonValue entrance : room.get("entrances")) {
                            JsonValue
                                roomOffset = entrance.get("offset"),
                                roomSize = entrance.get("offset");
                            String roomType = entrance.getString("type"),
                                roomSide = entrance.getString("side");
                            entrances.add(new EntranceBluprint() {{
                                offset = new Vector3(roomOffset.getFloat("x"), roomOffset.getFloat("y"), roomOffset.getFloat("z"));
                                size = new Vector3(roomSize.getFloat("x"), roomSize.getFloat("y"), roomSize.getFloat("z"));
                                type = Entrance.Type.valueOf(roomType);
                                side = EntranceBluprint.Side.valueOf(roomSide);
                            }});
                        }
                        staticObjects.get(staticObjects.size).setRoom(room.getFloat("thickness"), entrances);
                    }

                    objectList.add(staticObjects.get(staticObjects.size - 1));

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
                    objectList.add(groundObjects.get(groundObjects.size - 1));
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
                    objectList.add(targetObjects.get(targetObjects.size - 1));

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
                    objectList.add(kinematicObjects.get(kinematicObjects.size));

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
                    objectList.add(dynamicObjects.get(dynamicObjects.size));

                }
                break;
            }

            sceneManager.addScene(scene);
        }
        return objectList;
    }
}
