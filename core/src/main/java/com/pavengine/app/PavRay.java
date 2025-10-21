package com.pavengine.app;

import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.pavengine.app.PavGameObject.GameObject;

public class PavRay {
    public Ray ray;
    public Vector3 intersection = new Vector3();
    public float distance = 20;
    GameObject attachedObject;
    Vector3 direction = new Vector3(), start = new Vector3(), end = new Vector3(), offset = new Vector3();
    boolean attachObject = false;
    float amplitude;
    float maxDistance = 40f;
    float thickness;
    Color color;

    public PavRay(Vector3 position, Vector3 direction, float amplitude, float thickness, Color color) {
        this.start = position;
        this.direction = new Vector3(direction).nor();
        this.amplitude = amplitude;
        this.thickness = thickness;
        this.color = color;
        this.end = position;
        this.ray = new Ray(position, direction);
    }

    public PavRay(GameObject attachedObject, Vector3 offset, float amplitude, float thickness, Color color) {
        this.attachedObject = attachedObject;
        this.offset = offset;
        this.attachObject = true;
        this.amplitude = amplitude;
        this.thickness = thickness;
        this.color = color;
        this.ray = new Ray(attachedObject.pos, direction);
    }


    public Vector3 applyOffset(Vector3 origin, Vector3 offset, Quaternion rotation) {
        return new Vector3(origin).add(offset.cpy().mul(new Matrix4().set(rotation)));
    }

    public boolean isHitting(GameObject obj) {
        return Intersector.intersectRayBounds(ray, obj.bounds, intersection);
    }

    public void update(float delta) {


        if (attachObject) {
            direction.set(0, 0, 1); // forward instead of backward
            attachedObject.rotation.transform(direction).nor();
            start.set(applyOffset(attachedObject.pos, offset, attachedObject.rotation));
            this.ray = new Ray(start, direction);
        }


        for (GameObject obj : targetObjects) {

            if (PavIntersector.intersect(ray, obj.bounds, obj.scene.modelInstance.transform, intersection)) {
                obj.debugColor = new Color(Color.RED);
                distance = start.dst(intersection);
            } else {
                obj.debugColor = new Color(Color.BLUE);
            }
        }

//        for (GameObject obj : groundObjects) {
//
//            if (PavIntersector.intersect(ray, obj.bounds, obj.scene.modelInstance.transform, intersection)) {
//                obj.debugColor = new Color(Color.RED);
//                distance = start.dst(intersection);
//            } else {
//                obj.debugColor = new Color(Color.BLUE);
//            }
//        }


//        for (GameObject obj : staticObjects) {
//
//            if (PavIntersector.intersect(ray, obj.bounds, obj.scene.modelInstance.transform, intersection)) {
//                obj.debugColor = new Color(Color.RED);
//                distance = start.dst(intersection);
//            } else {
//                obj.debugColor = new Color(Color.BLUE);
//            }
//        }

//        for (Cell obj : pathFinder.grid) {
//            if (Intersector.intersectRayBounds(ray, obj.bounds, intersection)) {
//
//                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
//                    pathFinder.grid.removeValue(obj, true);
//                }
//
//                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
//                    if (obj.isStart) {
//                        obj.isStart = false;
//                        pathFinder.hasStart = false;
//                        return;
//                    }
//                    if (obj.isEnd) {
//                        obj.isEnd = false;
//                        pathFinder.hasEnd = false;
//                        return;
//                    }
//                    if (pathFinder.hasStart && !pathFinder.hasEnd) {
//                        obj.isEnd = true;
//                        pathFinder.endPosition.set(obj.coordinates);
//                        pathFinder.startPosition.set(obj.coordinates);
//                        pathFinder.hasEnd = true;
//                    }
//
//                    if (!pathFinder.hasEnd) {
//                        obj.isStart = true;
//                        pathFinder.currentCell.set(obj.coordinates);
//                        pathFinder.hasStart = true;
//                    }
//                }
//                obj.debugColor = new Color(Color.WHITE);
////                distance = start.dst(intersection);
//            } else {
//                obj.debugColor = new Color(Color.DARK_GRAY);
//            }
//        }

//        for(GameObject obj : staticObjects) {
//            print(obj.name+" : "+obj.distanceFrom(end));
//            end.set(start);
//        }
//
//        for(GameObject obj : kinematicObjects) {
//            print(obj.name+" : "+obj.distanceFrom(end));
//            end.set(start);
//        }

    }
}
