package com.pavengine.app.PavPlayer;

import com.badlogic.gdx.utils.Array;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavGameObject.KinematicObject;

public class PavPlayer {
    public static GameObject player = new KinematicObject();
    public static Array<PlayerBehavior> playerBehavior = new Array<>();

    public PavPlayer() {

    }

    public static void update(float delta) {
        for (PlayerBehavior b : playerBehavior) {
            b.update(player, delta);
        }
    }
}
