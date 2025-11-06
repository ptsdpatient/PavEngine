package com.pavengine.app;

import static com.pavengine.app.Debug.Draw.debugLine;
import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class ReferenceOriginRay {
    Vector3 origin = new Vector3(0,0,0);
    Vector3 endpoint;
    Color color;

    public ReferenceOriginRay(Vector3 endpoint, Color color) {
        this.color = color;
        this.endpoint = endpoint;
    }

    public void draw() {
        debugLine(origin, endpoint, color);
    }
}
