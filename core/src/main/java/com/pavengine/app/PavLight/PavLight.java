package com.pavengine.app.PavLight;

import static com.pavengine.app.PavScreen.GameWorld.sceneManager;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.PointLight;
import com.badlogic.gdx.math.Vector3;
import com.pavengine.app.PavGameObject.GameObject;

public class PavLight {
    public static PavLightProfile profile = PavLightProfile.NIGHT;
    public Environment environment;
    public GameObject attachedObject;
    public boolean attached = false;

    public PavLight(Environment environment, PavLightProfile profile) {
        this.environment = environment;
        this.profile = profile;
        sceneManager.setAmbientLight(0.02f);
        environment.add(new DirectionalLight().set(profile.color, profile.direction));
        environment.add(new DirectionalLight().set(Color.WHITE, new Vector3(5, -1, 0).nor()));


    }

    public void addPointLight(Color color, Vector3 position, float intensity) {
        environment.add(new PointLight().set(color, position, intensity));
    }


}
