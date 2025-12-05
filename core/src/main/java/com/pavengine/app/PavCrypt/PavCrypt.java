package com.pavengine.app.PavCrypt;


import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCrypt.DataMap.gameObjectCrypt;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavBounds.PavBoundsType;
import com.pavengine.app.PavGameObject.GameObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class PavCrypt {
    static JsonValue json;
    static FileHandle file;
    static DataInputStream in;
    static DataOutputStream out;

    public static void writeBounds(DataOutputStream out, PavBounds b) throws IOException {
        out.writeInt(b.type.ordinal());
        writeVec3(out, b.position);
        writeQuat(out, b.rotation);
        writeVec3(out, b.scale);
        writeVec3(out, b.center);
    }

    public static PavBounds readBounds(DataInputStream in) throws IOException {
        PavBounds b = new PavBounds();

        b.type = PavBoundsType.values()[in.readInt()];

        readVec3(in, b.position);
        readQuat(in, b.rotation);
        readVec3(in, b.scale);
        readVec3(in, b.center);
        b.rebuild();

        return b;
    }


    public static void cryptWrite(DataOutputStream out, Consumer<JsonValue> builder) {
        try {
            json = new JsonValue(JsonValue.ValueType.object);
            builder.accept(json);

            int count = 0;
            for (JsonValue child = json.child; child != null; child = child.next) count++;

            out.writeInt(count);

            for (JsonValue child = json.child; child != null; child = child.next) {
                CryptEnum type = CryptEnum.valueOf(child.get("type").asString());
                out.writeInt(type.id);
                cryptAdd(type, out, child.get("value"));
            }

        } catch (Exception e) {
            throw new RuntimeException("Error occurred: " + e);
        }
    }


    private static void cryptAdd(CryptEnum type, DataOutputStream out, JsonValue value) throws IOException {
        switch (type) {

            case Int:
                out.writeInt(value.getInt("value"));
                break;

            case Float:
                out.writeFloat(value.getFloat("value"));
                break;

            case String:
                String str = value.getString("value");
                out.writeInt(str.length());
                out.writeBytes(str);
                break;

            case Boolean:
                out.writeBoolean(value.getBoolean("value"));
                break;

            case Vector2:
                out.writeFloat(value.getFloat("x"));
                out.writeFloat(value.getFloat("y"));
                break;

            case Vector3:
                out.writeFloat(value.getFloat("x"));
                out.writeFloat(value.getFloat("y"));
                out.writeFloat(value.getFloat("z"));
                break;

            case Quaternion:
                out.writeFloat(value.getFloat("w"));
                out.writeFloat(value.getFloat("x"));
                out.writeFloat(value.getFloat("y"));
                out.writeFloat(value.getFloat("z"));
                break;
        }
    }

    public static <T> void writeArray(String path, Array<T> arr, CryptSchema schema)  {
        try {
            file = Gdx.files.local(path);
            out = new DataOutputStream(file.write(false));
            out.writeInt(arr.size);
            for (T item : arr) {
                switch (schema) {
                    case GameObject:
                        cryptWrite(out, gameObjectCrypt((GameObject) item));
                        break;
                }
            }
            out.close();
        } catch (Exception e) {
            print("Error ocurred! : " + e);
            throw new RuntimeException(e);
        }
    }

    public static <T> void readArray(String path, CryptSchema schema, Consumer<Map<String, Object>> onRead) {
        try (DataInputStream in = new DataInputStream(Gdx.files.local(path).read())) {

            int arraySize = in.readInt();

            for (int i = 0; i < arraySize; i++) {
                Map<String, Object> objMap = new HashMap<>();

                switch (schema) {
                    case GameObject:
                        readFields(in, objMap);
                        onRead.accept(objMap);
                        break;

                    // Add other schemas here in future
                    // case SomeOtherSchema:
                    //     readFields(in, objMap);
                    //     onRead.accept(objMap);
                    //     break;
                }
            }

        } catch (Exception e) {
            throw new RuntimeException("Error reading binary: " + e);
        }
    }

    private static void readFields(DataInputStream in, Map<String, Object> objMap) throws IOException {
        int fieldCount = in.readInt();

        for (int f = 0; f < fieldCount; f++) {
            int typeId = in.readInt();
            CryptEnum type = CryptEnum.fromId(typeId);

            Object value = readValueByType(in, type);
            objMap.put("field" + f, value);
        }
    }

    private static Object readValueByType(DataInputStream in, CryptEnum type) throws IOException {
        switch (type) {
            case Int:
                return in.readInt();
            case Float:
                return in.readFloat();
            case String: {
                int len = in.readInt();
                byte[] bytes = new byte[len];
                in.readFully(bytes);
                return new String(bytes);
            }
            case Boolean:
                return in.readBoolean();
            case Vector2:
                return new Vector2(in.readFloat(), in.readFloat());
            case Vector3:
                return new Vector3(in.readFloat(), in.readFloat(), in.readFloat());
            case Quaternion:
                float w = in.readFloat();
                float x = in.readFloat();
                float y = in.readFloat();
                float z = in.readFloat();
                return new Quaternion(x, y, z, w);
            default:
                throw new IllegalArgumentException("Unknown CryptEnum type: " + type);
        }
    }


    public static void writeArray(String name, Array<PavBounds> arr)  {
        try {
            file = Gdx.files.local("assets/models/" + name + "/bounds.bin");
            out = new DataOutputStream(file.write(false));
            out.writeInt(arr.size);
            for (PavBounds b : arr) writeBounds(out, b);
            out.close();
        } catch (Exception e) {
            print("Error ocurred! : " + e);
            throw new RuntimeException(e);
        }
    }

    public static Array<PavBounds> readArray(String name)  {
        try {
            file = Gdx.files.local("assets/models/" + name + "/bounds.bin");
            in = new DataInputStream(file.read());
            int size = in.readInt();
            Array<PavBounds> arr = new Array<>(size);
            for (int i = 0; i < size; i++)
                arr.add(readBounds(in));
            in.close();
            return arr;
        } catch (Exception e) {
            print("Error loading array : " + e);
            return new Array<>();
        }
    }

    private static void writeVec3(DataOutputStream out, Vector3 v) throws IOException {
        out.writeFloat(v.x);
        out.writeFloat(v.y);
        out.writeFloat(v.z);
    }

    private static void writeStringArray(DataOutputStream out, String[] s) throws IOException {
        for(String string : s) {
            out.writeChars(string);
        }
    }

    private static void writeFloatArray(DataOutputStream out, Float[] s) throws IOException {
        for(Float x : s) {
            out.writeFloat(x);
        }
    }


    private static void readVec3(DataInputStream in, Vector3 v) throws IOException {
        v.x = in.readFloat();
        v.y = in.readFloat();
        v.z = in.readFloat();
    }

    private static void writeQuat(DataOutputStream out, Quaternion q) throws IOException {
        out.writeFloat(q.x);
        out.writeFloat(q.y);
        out.writeFloat(q.z);
        out.writeFloat(q.w);
    }

    private static void readQuat(DataInputStream in, Quaternion q) throws IOException {
        q.x = in.readFloat();
        q.y = in.readFloat();
        q.z = in.readFloat();
        q.w = in.readFloat();
    }

    private static void writeInt(DataOutputStream out, int i) throws IOException {
        out.writeInt(i);
    }
}

