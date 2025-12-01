package com.pavengine.app;

import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class CameraKeyFrames {
    public float time;
    public Vector3 position;
    public Vector3 rotationAngles;
    public Quaternion rotation = new Quaternion();

    public CameraKeyFrames(float time, Vector3 position,Vector3 direction) {
        this.time = time;
        this.position = position;
        this.rotationAngles = direction;
        this.rotation.setEulerAngles(direction.x,direction.y,direction.z);
    }

    public void set(Vector3 position, float time ,Vector3 dir) {
        this.position = position;
        this.time = time;
        this.rotationAngles = dir;
        this.rotation.setEulerAngles(rotationAngles.x, rotationAngles.y, rotationAngles.z);
    }
}
