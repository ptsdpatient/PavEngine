package com.pavengine.app;

import com.badlogic.gdx.math.Vector3;

public class CameraTransform {
    public Vector3 position, direction;
    public CameraTransform(){
        position = new Vector3();
        direction = new Vector3();
    }
    public void set(Vector3 position,Vector3 direction) {
        this.position = position;
        this.direction = direction;
    }
}
