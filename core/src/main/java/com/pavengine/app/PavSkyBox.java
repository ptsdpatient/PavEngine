package com.pavengine.app;

import static com.pavengine.app.Methods.loadModel;
import static com.pavengine.app.PavEngine.sceneManager;

import com.badlogic.gdx.math.Vector3;

import net.mgsx.gltf.scene3d.scene.Scene;

public class PavSkyBox {
    Scene scene;

    public PavSkyBox(String model, Vector3 position, float scale) {
        this.scene = new Scene(loadModel("skybox/" + model + "/" + model + ".gltf").scene);
        sceneManager.addScene(scene);
        this.scene.modelInstance.transform.setToTranslation(position);
        scene.modelInstance.transform.scl(scale);
    }
}
