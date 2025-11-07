package com.pavengine.app;

import static com.pavengine.app.Debug.Draw.debugLine;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

public class ReferenceEditorLine {
    Vector3 origin, endpoint;
    Color color;

    public ReferenceEditorLine(
        Vector3 origin,
        Vector3 endpoint,
        Color color
    ) {
        this.origin = origin;
        this.color = color;
        this.endpoint = endpoint;
    }

    public void draw() {
        debugLine(
            origin,
            endpoint,
            color
        );
    }
}
