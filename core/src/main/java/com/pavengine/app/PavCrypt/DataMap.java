package com.pavengine.app.PavCrypt;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.JsonValue;
import com.pavengine.app.PavGameObject.GameObject;

import java.util.function.Consumer;

public class DataMap {

    static JsonValue getVector2Json(Vector2 vector2) {
        JsonValue json = new JsonValue(JsonValue.ValueType.object), value = new JsonValue(JsonValue.ValueType.object);
        json.addChild("type", new JsonValue("Vector2"));
        value.addChild("x", new JsonValue(vector2.x));
        value.addChild("y", new JsonValue(vector2.y));
        json.addChild("value", value);
        return json;
    }

    static JsonValue getVector3Json(Vector3 vector3) {
        JsonValue json = new JsonValue(JsonValue.ValueType.object), value = new JsonValue(JsonValue.ValueType.object);
        json.addChild("type", new JsonValue("Vector3"));
        value.addChild("x", new JsonValue(vector3.x));
        value.addChild("y", new JsonValue(vector3.y));
        value.addChild("z", new JsonValue(vector3.z));
        json.addChild("value", value);
        return json;
    }

    static JsonValue getQuaternionJson(Quaternion quaternion) {
        JsonValue json = new JsonValue(JsonValue.ValueType.object), value = new JsonValue(JsonValue.ValueType.object);
        json.addChild("type", new JsonValue("Quaternion"));
        value.addChild("w", new JsonValue(quaternion.w));
        value.addChild("x", new JsonValue(quaternion.x));
        value.addChild("y", new JsonValue(quaternion.y));
        value.addChild("z", new JsonValue(quaternion.z));
        json.addChild("value", value);
        return json;
    }

    static JsonValue getStringJson(String string) {
        JsonValue json = new JsonValue(JsonValue.ValueType.object), value = new JsonValue(JsonValue.ValueType.object);
        json.addChild("type", new JsonValue("String"));
        value.addChild("value", new JsonValue(string));
        json.addChild("value", value);
        return json;
    }

    public static Consumer<JsonValue> gameObjectCrypt(GameObject obj) {
        return json -> {
            json.addChild("name", getStringJson(obj.name));
            json.addChild("type", getStringJson(obj.objectType.name()));
            json.addChild("position", getVector3Json(obj.pos));
            json.addChild("rotation", getQuaternionJson(obj.rotation));
        };
    }
}
