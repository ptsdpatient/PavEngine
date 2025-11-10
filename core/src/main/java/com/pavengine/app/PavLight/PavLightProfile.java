package com.pavengine.app.PavLight;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;


public enum PavLightProfile {

    DAY(
        new Color(5f, 5f, 5f, 1f), // soft daylight
        new Vector3(new Vector3(-4f, -1, -4)).nor()           // medium ambient
    ),
    NIGHT(
        new Color(0.3f, 0.3f, 0.4f, 1f), // bluish dim light
        new Vector3(-5f, -40f, -10f)
    );

    public final Color color;
    public final Vector3 direction;

    PavLightProfile(Color color, Vector3 direction) {
        this.color = color;
        this.direction = direction;
    }
}
