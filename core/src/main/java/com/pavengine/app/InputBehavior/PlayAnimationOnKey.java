package com.pavengine.app.InputBehavior;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.AnimationController;
import com.pavengine.app.PavGameObject.GameObject;

public class PlayAnimationOnKey implements InputBehavior {
    boolean hold, loop, isPlaying = false;
    private int key;
    private int animationIndex;

    public PlayAnimationOnKey(int button, int animationIndex, boolean hold, boolean loop) {
        this.key = button;
        this.animationIndex = animationIndex;
        this.hold = hold;
        this.loop = loop;
    }

    @Override
    public void update(GameObject obj, float delta) {
        if (!hold && obj.animated && obj.currentAnimation == animationIndex && !Gdx.input.isKeyPressed(key)) {
            obj.animated = false;
            isPlaying = false;
            obj.scene.animationController.current.time = 0f;
            obj.playAnimation(0, false, true);
        }

        if (Gdx.input.isKeyJustPressed(key)) {

            if (hold) {
                obj.playAnimation(animationIndex, false, true);
            } else {
                obj.currentAnimation = animationIndex;
                obj.animated = true;
                isPlaying = true;
                obj.scene.animationController.setAnimation(obj.animationNames.get(animationIndex), loop ? -1 : 1, new AnimationController.AnimationListener() {
                    @Override
                    public void onEnd(AnimationController.AnimationDesc animation) {
                    }

                    @Override
                    public void onLoop(AnimationController.AnimationDesc animation) {

                    }
                });
            }
        }

    }
}
