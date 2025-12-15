package com.pavengine.app;

import static com.pavengine.app.PavScreen.GameWorld.staticObjects;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.PavGameObject.GameObject;

import java.util.Objects;

public class TransformTransition {
    public Vector3 position, direction;

    public TransformTransition() {
        position = new Vector3();
        direction = new Vector3();
    }

    public TransformTransition(String modelName) {
        position = new Vector3();
        direction = new Vector3();

        for(GameObject obj : staticObjects) {
            if(Objects.equals(obj.name, modelName)) {
                position = obj.pos.cpy();
                direction = obj.getDirection();
            }
        }

    }

    public void set(Vector3 position,Vector3 direction) {
        this.position = position;
        this.direction = direction;
    }
    public Array<Float> getPositionArray(){
        return new Array<>(
            new Float[]{
                position.x,
                position.y,
                position.z
            }
        );
    }
    public Array<Float> getDirectionArray(){
        return new Array<>(
            new Float[]{
                direction.x,
                direction.y,
                direction.z
            }
        );
    }
}
