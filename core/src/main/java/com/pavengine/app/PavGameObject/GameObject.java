package com.pavengine.app.PavGameObject;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.math.collision.OrientedBoundingBox;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Actions;
import com.pavengine.app.Direction;
import com.pavengine.app.EnemyAttackAction;
import com.pavengine.app.Force;
import com.pavengine.app.InputBehavior.InputBehavior;
import com.pavengine.app.InteractType;
import com.pavengine.app.ObjectBehaviorType;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.SlopeRay;

import net.mgsx.gltf.scene3d.scene.Scene;

import java.util.ArrayList;
import java.util.Random;

public abstract class GameObject {
    public final float VELOCITY_THRESHOLD = 10f;
    public final Random random = new Random();
    public boolean gravity = false;
    public boolean ringDetection = false;
    public int currentAnimation = 0;
    public Vector3 slopeNormal = new Vector3();
    public ArrayList<SlopeRay> slopeRays = new ArrayList<>();
    public ArrayList<EnemyAttackAction> enemyAttackActionList = new ArrayList<>();
    public EnemyAttackAction currentAttackAction;
    public Vector3 offset = new Vector3(0, 0, 0), forwardDirection = new Vector3(), padding = new Vector3(0, 0, 0), transform = new Vector3(), size = new Vector3(1, 1, 1), pos = new Vector3();
    public boolean grounded = false, hasBounced = false, animated = false;
    public float timeAlive = 0f, y = 0f, radius = 0f, vy = 0f, GRAVITY = -9.8f, K = 0.5f, testRotation = 0f;
    public Vector3 angularVelocity = new Vector3(), attackOffset = new Vector3(0, 0, 0);
    public float spinIntensity = 2.0f;
    public float angularDrag = 0.2f;
    public float mass, bounciness;
    public Array<Force> forces = new Array<>();
    public Array<Actions> actions = new Array<>();
    public Array<String> animationNames = new Array<>();
    public GameObject attachObject;
    public Color debugColor = new Color(Color.LIGHT_GRAY);
    public Quaternion rotation = new Quaternion(), deltaRotation = new Quaternion();
    public String name;
    public boolean isStatic = false;
    public boolean interactible = false, isRoom = false;
    public InteractType interactType;
    public boolean interacted = false;
    public Scene scene;
    public BoundingBox bounds;
    public PavBounds pavBounds = new PavBounds();
    public Array<PavBounds> boxes = new Array<>();
    public PavBounds footBox = new PavBounds(), attackBox = new PavBounds();
    public Vector3 center = new Vector3(), bottom = new Vector3(), dir = new Vector3();
    public ObjectType objectType;
    public ObjectBehaviorType objectBehaviorType = ObjectBehaviorType.Static;
    public boolean isEnemy = false;
    public boolean behaveIfCloseToPlayer = false;
    public float behaveRange, attackRange = 0;
    public float damage = 0, ringRadius = 0f, ringHeightOffset = 0f;
    public float fireRate = 100, fireTime = 0;
    public int[] attackAnimation = new int[]{};
    public ArrayList<InputBehavior> inputBehaviorList = new ArrayList<>();
    public boolean detectSlope = false;

    public GameObject() {

    }

    public abstract void setRing(float ringRadius, float ringHeightOffset);

    public abstract void setInteractAction(InteractType type);

    public abstract boolean checkCollision(Vector3 position);

    public abstract float distanceFrom(Vector3 position);

    public abstract boolean checkCollision(GameObject otherObject);

    public abstract void rotate(Direction direction, float amplitude);

    public abstract void updateCenter();

    public abstract void playAnimation(int index, boolean loop, boolean force);

    public abstract void playAnimation(String name, boolean loop, boolean force);

    public abstract void moveTowards(Vector3 target, float speed, float delta);

    public abstract void slopeDetection();

    public abstract void attachToObject(GameObject object, Vector3 offset);

    public abstract void attachToCamera(Vector3 offset);

    public void setEnemy(Vector3 attackOffset, float behaveRange, float attackRange, float fireRate, float damage, boolean behaveIfCloseToPlayer, int[] attackAnimation) {
        isEnemy = true;
        this.attackOffset = attackOffset;
        this.behaveRange = behaveRange;
        this.attackRange = attackRange;
        this.fireRate = fireRate;
        this.damage = damage;
        this.behaveIfCloseToPlayer = behaveIfCloseToPlayer;
        this.attackAnimation = attackAnimation;
    }

    public abstract void setScale(Vector3 size);

    public abstract void update(float delta);

    public abstract void updateBox();

    public abstract float getHeight();

    public abstract float getWidth();

    public abstract float getDepth();

    public abstract boolean contains(Vector3 point);


    public void setSlopeDetection() {
        slopeRays.add(new SlopeRay(new Vector3(0, (bounds.getWidth() * size.x) / 2f, 0)));           // center
        slopeRays.add(new SlopeRay(new Vector3(0, (bounds.getWidth() * size.x) / 2f, bounds.getWidth() * size.x)));      // forward
        slopeRays.add(new SlopeRay(new Vector3(bounds.getWidth() * size.x, (bounds.getWidth() * size.x) / 2f, 0)));      // right
    }

    public Vector3 getDirection() {
        return new Vector3(
            rotation.getYaw(),
            rotation.getPitch(),
            rotation.getRoll()
        );
    }
}
