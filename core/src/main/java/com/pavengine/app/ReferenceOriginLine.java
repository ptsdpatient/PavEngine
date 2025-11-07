package com.pavengine.app;

import static com.pavengine.app.Debug.Draw.debugLine;
import static com.pavengine.app.Methods.print;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class ReferenceOriginLine {
    float yOffset = 0f;
    Vector3 origin = new Vector3(0,0,0);
    Vector3 endpoint;
    Color color;

    public ReferenceOriginLine(Vector3 endpoint, Color color) {
        this.color = color;
        this.endpoint = endpoint;
        this.origin.y += yOffset;
        this.endpoint.y += yOffset;
    }

    public void draw() {
        debugLine(origin, endpoint, color);
    }
}
