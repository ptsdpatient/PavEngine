package com.pavengine.app.PavCrypt;


import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavBounds.PavBoundsType;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PavCrypt {

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

    public static void cryptWrite(CryptEnum[] order, String path) {
        try {
//            file = Gdx.files.local(path);
//            out = new DataOutputStream(file.write(false));
//            out.writeInt(arr.size);
//            for (PavBounds b : arr) writeBounds(out, b);
//            out.close();
        } catch (Exception e) {
            print("Error ocurred! : " + e);
            throw new RuntimeException(e);
        }
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

