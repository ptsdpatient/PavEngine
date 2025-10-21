package com.pavengine.app.PavLight;

import static com.pavengine.app.PavScreen.GameWorld.sceneManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.environment.SpotLight;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.pavengine.app.PavGameObject.GameObject;

public class PavTorch {

    public SpotLight light;
    public Color color;
    public boolean attached = false;
    public GameObject attachedObject;
    public Vector3 offset, forwardDirection;

    public PavTorch(Color color, Vector3 position, Vector3 direction, float intensity, float cutOffAngle, float exponent) {
        light = new SpotLight().set(color, position, direction, intensity, cutOffAngle, exponent);
        sceneManager.environment.add(light);
    }

    public PavTorch(GameObject object, Vector3 offset, Color color, float intensity, float cutOffAngle, float exponent) {
        light = new SpotLight().set(color, object.pos.cpy(), new Vector3(0, 0, -1).rot(new Matrix4().set(object.rotation)), intensity, cutOffAngle, exponent);
        sceneManager.environment.add(light);
        attached = true;
        attachedObject = object;
        this.offset = offset;
    }

    public void update(float delta) {
        Quaternion totalRotation = new Quaternion(attachedObject.rotation);


        Quaternion offsetRotation = new Quaternion().setEulerAngles(
            MathUtils.degreesToRadians * 90f,
            MathUtils.degreesToRadians * 180,
            0f
        );
        totalRotation.mul(offsetRotation);

        Vector3 rotatedOffset = offset.cpy().rot(new Matrix4().set(totalRotation));
        light.position.set(attachedObject.pos).add(rotatedOffset);

        light.direction.set(new Vector3(0, 0, -1).rot(new Matrix4().set(totalRotation))).nor();


    }

}
