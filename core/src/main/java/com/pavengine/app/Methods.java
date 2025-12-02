package com.pavengine.app;

import static com.badlogic.gdx.math.MathUtils.clamp;
import static com.pavengine.app.CameraTransitionMode.AXIS_PRIORITY;
import static com.pavengine.app.CameraTransitionMode.BEZIER_XZ;
import static com.pavengine.app.CameraTransitionMode.BEZIER_Y;
import static com.pavengine.app.CameraTransitionMode.STAGGERED;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavScreen.GameWorld.dynamicObjects;
import static com.pavengine.app.PavScreen.GameWorld.groundObjects;
import static com.pavengine.app.PavScreen.GameWorld.kinematicObjects;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.CubemapData;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FacedCubemapData;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;
import com.pavengine.app.PavBounds.PavBounds;
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
import net.mgsx.gltf.scene3d.scene.SceneSkybox;

import java.util.ArrayList;

public class Methods {


    public static void print(String value) {
        Gdx.app.log("Game", value);
    }

    public static void print(Vector2 value) {
        Gdx.app.log("Game", value + "");
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

//    public static Vector3 getEulerAngles(Quaternion q) {
//        Vector3 angles = new Vector3();
//
//        // Pitch (X-axis rotation)
//        float sinp = 2f * (q.w * q.x + q.y * q.z);
//        float cosp = 1f - 2f * (q.x * q.x + q.y * q.y);
//        angles.x = MathUtils.atan2(sinp, cosp) * MathUtils.radiansToDegrees;
//
//        // Yaw (Y-axis rotation)
//        float siny = 2f * (q.w * q.y - q.z * q.x);
//        if (Math.abs(siny) >= 1)
//            angles.y = Math.copySign(90f, siny); // clamp at ±90°
//        else
//            angles.y = MathUtils.asin(siny) * MathUtils.radiansToDegrees;
//
//        // Roll (Z-axis rotation)
//        float sinr = 2f * (q.w * q.z + q.x * q.y);
//        float cosr = 1f - 2f * (q.y * q.y + q.z * q.z);
//        angles.z = MathUtils.atan2(sinr, cosr) * MathUtils.radiansToDegrees;
//
//        return angles;
//    }

    public static Vector3 getEulerAngles(Quaternion q) {
        Vector3 angles = new Vector3();

        float qw = q.w;
        float qx = q.x;
        float qy = q.y;
        float qz = q.z;

        // Yaw (Y-axis rotation)
        angles.y = (float) Math.atan2(2.0 * (qw * qy + qx * qz), 1 - 2.0 * (qy * qy + qz * qz));

        // Pitch (X-axis rotation)
        double sinp = 2.0 * (qw * qx - qz * qy);
        if (Math.abs(sinp) >= 1)
            angles.x = (float) Math.copySign(Math.PI / 2, sinp); // use 90 degrees if out of range
        else
            angles.x = (float) Math.asin(sinp);

        // Roll (Z-axis rotation)
        angles.z = (float) Math.atan2(2.0 * (qw * qz + qx * qy), 1 - 2.0 * (qz * qz + qx * qx));

        // Convert radians to degrees if needed
        angles.scl(MathUtils.radiansToDegrees);

        return angles;
    }


//    public static boolean transitionCamera(CameraTransform start,
//                                           CameraTransform end,
//                                           float elapsed,
//                                           float duration) {
//
//        if (elapsed >= duration) {
//            // Snap to final state
//            camera.position.set(end.position);
//            camera.direction.set(end.direction);
//            camera.update();
//            return false;
//        }
//
//        float t = elapsed / duration;
//
//        // Ease In-Out smoothing
//        t = t * t * (3f - 2f * t);    // smoothstep (0→1→0)
//
//        // Interpolate position + direction
//        camera.position.set(
//            start.position).lerp(end.position, t);
//
//        camera.direction.set(
//            start.direction).lerp(end.direction, t);
//
//        camera.update();
//        return true;
//    }

    public static boolean transitionCamera(CameraTransform start,
                                           CameraTransform end,
                                           float elapsed,
                                           float duration,
                                           CameraTransitionMode mode) {

        if (elapsed >= duration) {
            camera.position.set(end.position);
            camera.direction.set(end.direction);
            camera.update();
            return false;
        }

        float t = elapsed / duration;

        // Compute interpolation factor depending on selected mode
        t = applyMode(t, mode);

        // Position
        Vector3 pos = interpolateVector(start.position, end.position, t, mode);

        // Direction
        Vector3 dir = interpolateVector(start.direction, end.direction, t, mode);

        camera.position.set(pos);
        camera.direction.set(dir);
        camera.update();

        return true;
    }

    private static float applyMode(float t, CameraTransitionMode mode) {

        switch (mode) {

            case LINEAR:
                return t;

            case SMOOTHSTEP:
                return t * t * (3f - 2f * t);

            case EASE_IN:
                return t * t;

            case EASE_OUT:
                return (float)Math.sqrt(t);

            case EASE_IN_OUT:
                return t * t * (3f - 2f * t); // same as smoothstep

            default:
                return t;
        }
    }


    private static Vector3 interpolateVector(Vector3 a, Vector3 b, float t, CameraTransitionMode mode) {
        Vector3 out = new Vector3();

        switch (mode) {
            case BEZIER_Y:
                // Y moves faster, X and Z remain linear
                float yT = bezierCurve(t);   // fast curve
                out.x = MathUtils.lerp(a.x, b.x, t);
                out.y = MathUtils.lerp(a.y, b.y, yT);
                out.z = MathUtils.lerp(a.z, b.z, t);
                return out;

            case BEZIER_XZ:
                float curve = bezierCurve(t);
                out.x = MathUtils.lerp(a.x, b.x, curve);
                out.y = MathUtils.lerp(a.y, b.y, t);
                out.z = MathUtils.lerp(a.z, b.z, curve);
                return out;

            case AXIS_PRIORITY:
                // X finishes first, then Y, then Z
                float tx = clamp(t * 1.5f);
                float ty = clamp((t - 0.33f) * 1.5f);
                float tz = clamp((t - 0.66f) * 3f);

                out.x = MathUtils.lerp(a.x, b.x, tx);
                out.y = MathUtils.lerp(a.y, b.y, ty);
                out.z = MathUtils.lerp(a.z, b.z, tz);
                return out;

            case STAGGERED:
                // X starts immediately, Y after 0.25, Z after 0.5
                float lx = clamp(t);
                float ly = clamp((t - 0.25f) / 0.75f);
                float lz = clamp((t - 0.5f) / 0.5f);

                out.x = MathUtils.lerp(a.x, b.x, lx);
                out.y = MathUtils.lerp(a.y, b.y, ly);
                out.z = MathUtils.lerp(a.z, b.z, lz);
                return out;

            default:
                // Default: uniform interpolation
                return out.set(a).lerp(b, t);
        }
    }

    private static float clamp(float v) {
        return MathUtils.clamp(v, 0f, 1f);
    }

    private static float bezierCurve(float t) {
        // simple cubic Bezier (0,0) → (0.3,1) → (0.7,1) → (1,1)
        return t * t * (3f - 2f * t);
    }


    public static TextureRegion[] extractSprites(String path, int width, int height) {
        TextureRegion sheet = new TextureRegion(new Texture(load(path)));
        return sheet.split(width, height)[0];
    }

    public static void lockCursor(boolean lock) {
        enableCursor = !lock;
        cursor.setCursor(lock ? 0 : 1);
        cursor.position = new Vector2(resolution.x/2f, resolution.y/2f);
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


    public static SceneSkybox createSkybox(String path) {
        Pixmap src = new Pixmap(load(path));
        int w = src.getWidth(), h = src.getHeight();
        int faceW = w / 4, faceH = h / 3;

        Pixmap posX = new Pixmap(faceW, faceH, Pixmap.Format.RGB888);
        Pixmap negX = new Pixmap(faceW, faceH, Pixmap.Format.RGB888);
        Pixmap posY = new Pixmap(faceW, faceH, Pixmap.Format.RGB888);
        Pixmap negY = new Pixmap(faceW, faceH, Pixmap.Format.RGB888);
        Pixmap posZ = new Pixmap(faceW, faceH, Pixmap.Format.RGB888);
        Pixmap negZ = new Pixmap(faceW, faceH, Pixmap.Format.RGB888);

        posX.drawPixmap(src, 0, 0, 2 * faceW, faceH, faceW, faceH);
        negX.drawPixmap(src, 0, 0, 0, faceH, faceW, faceH);
        posY.drawPixmap(src, 0, 0, faceW, 0, faceW, faceH);
        negY.drawPixmap(src, 0, 0, faceW, 2 * faceH, faceW, faceH);
        posZ.drawPixmap(src, 0, 0, faceW, faceH, faceW, faceH);
        negZ.drawPixmap(src, 0, 0, 3 * faceW, faceH, faceW, faceH);

        src.dispose();

        CubemapData data = new FacedCubemapData(posX, negX, posY, negY, posZ, negZ);
        Cubemap cubemap = new Cubemap(data);
        cubemap.load(data);

        return new SceneSkybox(cubemap);
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

//                    if (room.getBoolean("isRoom")) {
//                        ArrayList<EntranceBluprint> entrances = new ArrayList<>();
//                        for (JsonValue entrance : room.get("entrances")) {
//                            JsonValue
//                                roomOffset = entrance.get("offset"),
//                                roomSize = entrance.get("offset");
//                            String roomType = entrance.getString("type"),
//                                roomSide = entrance.getString("side");
//                            entrances.add(new EntranceBluprint() {{
//                                offset = new Vector3(roomOffset.getFloat("x"), roomOffset.getFloat("y"), roomOffset.getFloat("z"));
//                                size = new Vector3(roomSize.getFloat("x"), roomSize.getFloat("y"), roomSize.getFloat("z"));
//                                type = Entrance.Type.valueOf(roomType);
//                                side = EntranceBluprint.Side.valueOf(roomSide);
//                            }});
//                        }
//                        staticObjects.get(staticObjects.size).setRoom(room.getFloat("thickness"), entrances);
//                    }

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
