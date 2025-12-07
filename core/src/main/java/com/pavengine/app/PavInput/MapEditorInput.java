package com.pavengine.app.PavInput;

import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavCrypt.PavCrypt.writeArray;
import static com.pavengine.app.PavEngine.axisGizmo;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.editorSelectedObjectBehavior;
import static com.pavengine.app.PavEngine.editorSelectedObjectText;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.perspectiveTouchRay;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavScreen.BoundsEditor.boundsLister;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameScreen.world;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;
import static com.pavengine.app.PavScreen.MapEditor.mapEditingLayout;
import static com.pavengine.app.PavScreen.MapEditor.sceneName;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.pavengine.app.EditorSelectedObjectBehavior;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavCrypt.CryptSchema;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavIntersector;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;

public class MapEditorInput {
    public static InputProcessor mapEditorInput = new InputProcessor() {
        private Vector3 activeAxis = Vector3.Zero;
        private TransformMode transformMode = TransformMode.NONE;

        Plane dragPlane = new Plane();
        Vector3 dragOffset = new Vector3();
        Vector3 perspectiveTouch = new Vector3(), overlayTouch = new Vector3();
        Quaternion initialRotation = new Quaternion();
        float initialSize = 1;
        Vector3 initialPosition = new Vector3(0, 0, 0);
        Vector3 startPointerPos = new Vector3();
        Vector3 initialScale = new Vector3();
        Vector3 intersection_scale = new Vector3();
        private final Vector3 tempVec1 = new Vector3();
        private final Vector3 newScale = new Vector3();


        @Override
        public boolean keyDown(int keycode) {
            if (selectedObject != null) switch (keycode) {
                case Input.Keys.G:
                    if (transformMode != TransformMode.NONE) {
                        print("none");
                        transformMode = TransformMode.NONE;
                        setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
                        break;
                    }

                    initialPosition.set(selectedObject.center);
                    transformMode = TransformMode.MOVE;
                    dragPlane = new Plane(camera.direction, initialPosition);

                    Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, startPointerPos);
                    dragOffset.set(selectedObject.pos).sub(startPointerPos);

                    setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.Grab);
                    activeAxis = Vector3.Zero;
                    break;
                case Input.Keys.S:
                    if (transformMode != TransformMode.NONE) {
                        transformMode = TransformMode.NONE;
                        setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
                        break;
                    }
                    activeAxis = Vector3.Zero;
                    transformMode = TransformMode.SCALE;
                    initialPosition = selectedObject.center;
                    setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.Scale);
                    dragPlane = new Plane(camera.direction, initialPosition);

                    if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, intersection_scale)) {
                        startPointerPos.set(intersection_scale);
                        initialScale.set(selectedObject.size);
                    }
                    break;
                case Input.Keys.R:
                    if (transformMode != TransformMode.NONE) {
                        transformMode = TransformMode.NONE;
                        setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
                        break;
                    }
                    setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.Rotate);
                    activeAxis = Vector3.Zero;
                    transformMode = TransformMode.ROTATE;
                    initialPosition.set(selectedObject.center);
                    dragPlane = new Plane(camera.direction, initialPosition);
                    dragOffset.set(new Vector3());
                    break;
                case Input.Keys.X:
                    if (activeAxis == Vector3.X) {
                        activeAxis = Vector3.Zero;
                        break;
                    }
                    activeAxis = Vector3.X;
                    break;
                case Input.Keys.Y:
                    if (activeAxis == Vector3.Y) {
                        activeAxis = Vector3.Zero;
                        break;
                    }
                    activeAxis = Vector3.Y;
                    break;
                case Input.Keys.Z:
                    if (activeAxis == Vector3.Z) {
                        activeAxis = Vector3.Zero;
                        break;
                    }
                    activeAxis = Vector3.Z;
                    break;
                case Input.Keys.ESCAPE:
                    transformMode = TransformMode.NONE;
                    setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
                    break;
                case Input.Keys.FORWARD_DEL:
                    print("delete");
                    staticObjects.removeValue(selectedObject, true);
                    selectedObject = null;
                    break;
            }
            return false;
        }

        @Override
        public boolean keyUp(int keycode) {

            if (keycode == Input.Keys.DEL || keycode == Input.Keys.FORWARD_DEL) {
                if (selectedObject != null) {
                    sceneManager.removeScene(selectedObject.scene);
                    staticObjects.removeValue(selectedObject, true);
                }
            }

            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {

            if(button != Input.Buttons.LEFT) return false;
            setPerspectiveTouch();

            for (PavLayout layout : mapEditingLayout) {


                for (PavWidget widget : layout.widgets) {

                    if (cursor.clicked(widget.box)) {

                        switch (widget.clickBehavior) {

                            case ExportModelInfo: {
                                writeArray(
                                    "assets/scenes/" + sceneName + ".bin",
                                    staticObjects,
                                    CryptSchema.GameObject
                                );
                                return true;
                            }

                            case AddStaticObjectToMapEditor: {
                                print("add : " + widget.text);
                                world.addObject(widget.text, widget.text, new Vector3(0, 0, 0), 1, 10, 1, ObjectType.STATIC, new String[]{""});
                                setSelectedObject(staticObjects.peek());
                                print(selectedObject == null ? "null" : "exists");
                                return true;
                            }
                            case ExitGame: {
                                Gdx.app.exit();
                            }

                            break;
                        }
                    }
                }
            }


            for (GameObject obj : staticObjects) {
                if (PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
                    transformMode = TransformMode.NONE;
                    if (selectedObject == obj) {
                        selectedObject = null;
                        return true;
                    }
                    setSelectedObject(obj);
                    dragPlane = new Plane(camera.direction, selectedObject.pos);
                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
                    return true;
                }
            }

            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {

            cursor.setCursor(1);

            if (!enableCursor) {
                lockCursor(false);
                Gdx.input.setCursorCatched(true);
            }

            setPerspectiveTouch();

            if (editorSelectedObjectBehavior != EditorSelectedObjectBehavior.FreeLook) {
                setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
            }


            if (button == Input.Buttons.LEFT) {
                if (cursor.clicked(axisGizmo.xRect)) {
                    axisGizmo.lookFromAxis(Vector3.X);
                    return true;
                } else if (cursor.clicked(axisGizmo.yRect)) {
                    axisGizmo.lookFromAxis(new Vector3(0, 0.8f, 0.1f));
                    return true;
                } else if (cursor.clicked(axisGizmo.zRect)) {
                    axisGizmo.lookFromAxis(Vector3.Z);
                    return true;
                }


                if (selectedObject != null)
                    if (!PavIntersector.intersect(perspectiveTouchRay, selectedObject.bounds, selectedObject.scene.modelInstance.transform, perspectiveTouch)) {
                        selectedObject.debugColor = Color.YELLOW;
                        selectedObject = null;
                    }
            }
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {

            setPerspectiveTouch();


            if (enableCursor) cursor.setCursor(2);


            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {

            setPerspectiveTouch();

            if (selectedObject != null) {

                switch (transformMode) {
                    case NONE:
                        break;
                    case MOVE:
                        Vector3 intersection = new Vector3();
                        Vector3 originalPos = initialPosition.cpy();

                        if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, intersection)) {
                            if (!activeAxis.isZero()) {
                                print(initialPosition);
                                if (activeAxis.equals(Vector3.X))
                                    intersection.set(intersection.x, originalPos.y, originalPos.z);
                                else if (activeAxis.equals(Vector3.Y))
                                    intersection.set(originalPos.x, intersection.y, originalPos.z);
                                else if (activeAxis.equals(Vector3.Z))
                                    intersection.set(originalPos.x, originalPos.y, intersection.z);
                            }

                            selectedObject.pos.set(intersection.add(dragOffset));
                        }
                        break;
                    case SCALE:
                        if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, intersection_scale)) {

                            float currentDistance = intersection_scale.dst(selectedObject.pos.cpy());

                            float startDistance   = startPointerPos.dst(selectedObject.pos.cpy());

                            if (startDistance > 0.0001f) {

                                float scaleFactor = currentDistance / startDistance;

                                newScale.set(initialScale).scl(scaleFactor);

                                newScale.x = Math.max(newScale.x, 0.1f);
                                newScale.y = Math.max(newScale.y, 0.1f);
                                newScale.z = Math.max(newScale.z, 0.1f);

                                selectedObject.size.set(newScale);
                            }
                        }

                        break;

                    case ROTATE:

                        Quaternion q = new Quaternion();

                        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                            q.set(Vector3.X, -Gdx.input.getDeltaY() * 0.75f);
                            selectedObject.rotation.mulLeft(q);
                        } else {
                            q.set(Vector3.Y, -Gdx.input.getDeltaX() * 0.75f);
                            selectedObject.rotation.mulLeft(q);
                        }

                        selectedObject.rotation.nor();
                        break;
                }
                selectedObject.updateBox();
                selectedObject.updateCenter();
            }

            boundsLister.buttonHovered = cursor.clicked(boundsLister.buttonRect);

            if (cursor.clicked(boundsLister.box)) {
                if (!boundsLister.dropDownExpand && boundsLister.buttonHovered)
                    boundsLister.dropDownExpand = true;
            } else if (boundsLister.dropDownExpand) {
                boundsLister.dropDownExpand = false;

            }


            for (GameObject obj : staticObjects) {
                boolean hit = PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch);
                obj.debugColor = hit ? Color.ORANGE : Color.YELLOW;
            }

            if (selectedObject != null) selectedObject.debugColor = Color.ORANGE;

            for (GameObject obj : targetObjects) {
                obj.debugColor = PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch) ? Color.ORANGE : Color.YELLOW;
            }

            for (PavLayout layout : mapEditingLayout) {
                layout.isHovered = cursor.clicked(layout.box);

                if (layout.isHovered) {
                    for (PavWidget widget : layout.widgets) {
                        widget.isHovered = cursor.clicked(widget.box);
                    }
                } else {
                    for (PavWidget widget : layout.widgets) {
                        if (widget.isHovered) {
                            widget.isHovered = false;
                        }
                    }
                }
            }


            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {

            for (PavLayout layout : mapEditingLayout)
                if (layout.isHovered) {
                    float layoutScrollAmount = amountY * 25;

                    if (layout.overflowY
                        && layout.topY >= (-30 - layoutScrollAmount)
                    ) {
                        layout.topY += layoutScrollAmount;
                        return false;
                    }

                    if (layout.overflowX
                        && layout.leftX <= -layoutScrollAmount
                    ) {
                        layout.leftX += layoutScrollAmount;
                        return false;
                    }

                }

            return false;
        }
    };

    private static void setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior value) {
//        PavEngine.editorSelectedObjectBehavior = PavEngine.editorSelectedObjectBehavior == value? EditorSelectedObjectBehavior.FreeLook: value;
        PavEngine.editorSelectedObjectBehavior = value;
        editorSelectedObjectText.text = PavEngine.editorSelectedObjectBehavior.name();


    }

    private static void setSelectedObject(GameObject obj) {
//        setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
        selectedObject = obj;
    }

    private static void setPerspectiveTouch() {

        Vector3 screenPos = overlayViewport.project(new Vector3(
            cursor.clickArea.getX() + cursor.clickArea.getWidth() / 2f,
            cursor.clickArea.getY() + cursor.clickArea.getHeight() / 2f,
            0
        ));

        screenPos.y = Gdx.graphics.getHeight() - screenPos.y;

        perspectiveTouchRay.set(camera.getPickRay(screenPos.x, screenPos.y));
    }

}
