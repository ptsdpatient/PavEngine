package com.pavengine.app.PavPlayer;

import static com.pavengine.app.Debug.Draw.debugCube;
import static com.pavengine.app.Methods.getEulerAngles;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavPlayer.PavPlayer.player;
import static com.pavengine.app.PavScreen.GameWorld.groundObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.Ray;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavGameObject.GameObject;

public class Jump implements PlayerBehavior {

    boolean jumping = true;
    float velocityY = 0f;
    float jumpStrength = 18f;
    float gravity = -20f;

    private static final Vector3 tmpX = new Vector3();
    private static final Vector3 tmpY = new Vector3();
    private static final Vector3 tmpZ = new Vector3();


//    public void setRotationToPlane(Quaternion rotation, Quaternion targetRotation) {
//        rotation.setEulerAngles(targetRotation.getYaw(), targetRotation.getPitch() , targetRotation.getRoll());
//    }


    public void setRotationToPlane() {

    }



    @Override
    public void update(GameObject player, float delta) {
        debugCube(player.footBox);

        boolean grounded = false;

        for (GameObject obj : groundObjects) {
            for(PavBounds box : obj.boxes)
                if (player.footBox.ringOverlaps(box,player.pos)) {

                    grounded = true;

                    if (velocityY <= 0) {
                        velocityY = 0f;
                        jumping = false;
                    }
                    break;
                }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && grounded) {
//            player.playAnimation(0, false, false);
            jumping = true;
            velocityY = jumpStrength;
        }

        if (!grounded) {
            jumping = true;
        }

        if (jumping) {
            velocityY += gravity * delta;
            player.pos.y += velocityY * delta;
        }
    }

}
