package com.pavengine.app;

import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.overlayCamera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PavParticle2D {

    public Vector3 worldPos;
    public boolean is3d = false;
    private Array<ParticleEffectPool.PooledEffect> effects = new Array<>();
    private ParticleEffectPool pool;

    public PavParticle2D(String particlePath, String imagesDir) {
        ParticleEffect effect = new ParticleEffect();
        effect.load(Gdx.files.internal(particlePath), Gdx.files.internal(imagesDir));
        pool = new ParticleEffectPool(effect, 5, 20);
    }

    public void spawn(float x, float y, float distanceFromCamera) {
        ParticleEffectPool.PooledEffect e = pool.obtain();

        float scale = MathUtils.clamp(10f / (distanceFromCamera + 1f), 0.4f, 2f);
        e.scaleEffect(scale);

        e.setPosition(x, y);
        effects.add(e);

    }

    public void spawn(Vector3 worldPos) {
        this.worldPos = worldPos.cpy();

        ParticleEffectPool.PooledEffect e = pool.obtain();

        float distance = camera.position.dst(worldPos);
        float scale = MathUtils.clamp(1f / (distance * 0.03f), 0.5f, 8f); // smaller factor → bigger effect
        e.scaleEffect(scale);


        effects.add(e);
        is3d = true;
    }

    public void update(SpriteBatch sb, float delta) {
        for (int i = effects.size - 1; i >= 0; i--) {
            ParticleEffectPool.PooledEffect e = effects.get(i);


            if (is3d) {
                Vector3 screenPos = camera.project(worldPos.cpy());
                float x = screenPos.x / Gdx.graphics.getWidth() * overlayCamera.viewportWidth;
                float y = screenPos.y / Gdx.graphics.getHeight() * overlayCamera.viewportHeight;

                e.setPosition(x, y);
            }

            e.draw(sb, delta);

            if (e.isComplete()) {
                e.free();
                effects.removeIndex(i);
            }
        }
    }
}
