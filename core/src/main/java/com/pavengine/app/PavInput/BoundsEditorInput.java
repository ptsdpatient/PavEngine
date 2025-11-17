package com.pavengine.app.PavInput;

import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavCrypt.PavCrypt.readArray;
import static com.pavengine.app.PavCrypt.PavCrypt.writeArray;
import static com.pavengine.app.PavEngine.axisGizmo;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.editorSelectedObjectText;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.pavCamera;
import static com.pavengine.app.PavEngine.perspectiveTouchRay;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavScreen.BoundsEditor.boundsEditorLayout;
import static com.pavengine.app.PavScreen.BoundsEditor.boundsLister;
import static com.pavengine.app.PavScreen.BoundsEditor.selectedBound;
import static com.pavengine.app.PavScreen.GameScreen.mapEditorPanel;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameScreen.world;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.ObjectMap;
import com.pavengine.app.EditorSelectedObjectBehavior;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavCursor;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavIntersector;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BoundsEditorInput {
    enum TransformMode {NONE, MOVE, SCALE, ROTATE}

    public static InputProcessor boundsEditorInput = new InputProcessor() {
        private TransformMode transformMode = TransformMode.NONE;
        private Vector3 activeAxis = Vector3.Zero;
        Plane dragPlane = new Plane();
        Vector3 dragOffset = new Vector3();
        Vector3 perspectiveTouch = new Vector3(), overlayTouch = new Vector3();
        Quaternion initialRotation = new Quaternion();
        float initialSize = 1;
        Vector3 initialPosition = new Vector3(0, 0, 0);

        @Override
        public boolean keyDown(int keycode) {


            if(selectedBound != null) switch (keycode) {
                case Input.Keys.G:
                    if (transformMode != TransformMode.NONE) {
                        transformMode = TransformMode.NONE;
                        setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
                        break;
                    }
                    initialPosition = selectedBound.getCenter();
                    transformMode = TransformMode.MOVE;
                    dragPlane = new Plane(camera.direction, initialPosition);
                    dragOffset.set(new Vector3());
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
                    initialPosition = selectedBound.getCenter();
                    setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.Scale);
                    dragPlane = new Plane(camera.direction, initialPosition);
                    dragOffset.set(new Vector3());
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
                    initialPosition = selectedBound.getCenter();
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
                    selectedObject.boxes.removeValue(selectedBound,true);
                    selectedBound = null;
                    break;
            }
            return true;
        }

        @Override
        public boolean keyUp(int keycode) {

            if (keycode == Input.Keys.ESCAPE) {
                Gdx.input.setCursorCatched(!enableCursor);
                lockCursor(enableCursor);
            }


            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            if(selectedObject != null) {
                setPerspectiveTouch();

                if (button == Input.Buttons.LEFT) {
                    for (PavBounds obj : selectedObject.boxes) {
                        if (
                            PavIntersector.intersect(perspectiveTouchRay,obj.box.getBounds(),obj.transform,perspectiveTouch)
                        ) {
                            if(selectedBound != null) {
                                initialPosition = selectedBound.getCenter();
                                dragPlane = new Plane(camera.direction, initialPosition);
                                dragOffset.set(new Vector3());
                            }

                            selectedBound = selectedBound == null ? obj : null;
                            transformMode = TransformMode.NONE;
                            setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);

                            return true;
                        }
                    }
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

            for (PavLayout layout : boundsEditorLayout) {

                for (PavWidget widget : layout.widgets) {

                    if (cursor.clicked(widget.box)) {

                        switch (widget.clickBehavior) {
                            case SaveBoundsArray : {
                                if(selectedObject != null) {
                                    writeArray(selectedObject.name,selectedObject.boxes);
                                }

                            } break;
                            case AddStaticObjectToMapEditor: {
                                if(selectedObject != null) {
                                    staticObjects.peek().boxes.clear();
                                    sceneManager.removeScene(staticObjects.peek().scene);
                                }
//                                print("add : " + widget.text);
                                world.addObject(widget.text, widget.text, new Vector3(0, 0, 0), 1, 10, 1, ObjectType.STATIC, new String[]{""});
                                setSelectedObject(staticObjects.get(staticObjects.size - 1));
                                selectedObject.boxes.addAll(readArray(selectedObject.name));
//                                print(selectedObject == null ? "null" : "exists");
                                return true;
                            }
                            case ExitGame: {
                                Gdx.app.exit();
                            }

                            break;
                        }
                        return true;
                    }
                }
            }

            setPerspectiveTouch();

            if (button == Input.Buttons.LEFT) {

                if (selectedBound != null) {
                    if (
                        !PavIntersector.intersect(perspectiveTouchRay,selectedBound.box.getBounds(),selectedBound.transform,perspectiveTouch)

                    ) {
                        if(selectedBound != null) {
                            initialPosition = selectedBound.getCenter();
                            dragPlane = new Plane(camera.direction, initialPosition);
                            dragOffset.set(new Vector3());
                        }
                        selectedBound = null;
                        transformMode = TransformMode.NONE;
                        setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
                    }
                }


                if (cursor.clicked(axisGizmo.xRect)) {
                    axisGizmo.lookFromAxis(Vector3.X);
                    return true;
                } else if (cursor.clicked(axisGizmo.yRect)) {
                    axisGizmo.lookFromAxis(new Vector3(0, -0.8f, 0.1f));
                    return true;
                } else if (cursor.clicked(axisGizmo.zRect)) {
                    axisGizmo.lookFromAxis(Vector3.Z);
                    return true;
                }

                if (cursor.clicked(mapEditorPanel))
                    return true;



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

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (selectedBound != null) {
                    for (PavLayout layout : boundsEditorLayout) {
                        if (cursor.clicked(layout.box))
                            return true;
                    }
                }
            }

            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {

            if(selectedObject != null) {
                setPerspectiveTouch();

                if (selectedBound != null) {
                    switch (transformMode) {
                        case NONE:
                            break;
                        case MOVE:
                            Vector3 intersection = new Vector3();
                            if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, intersection)) {
                                if (!activeAxis.isZero()) {
                                    if (activeAxis.equals(Vector3.X))
                                        intersection.set(intersection.x, initialPosition.y, initialPosition.z);
                                    else if (activeAxis.equals(Vector3.Y))
                                        intersection.set(initialPosition.x, intersection.y, initialPosition.z);
                                    else if (activeAxis.equals(Vector3.Z))
                                        intersection.set(initialPosition.x, initialPosition.y, intersection.z);
                                }

                                selectedBound.setPosition(intersection);
                            }
                            break;
                        case SCALE:
                            Vector3 intersection_scale = new Vector3();
                            if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, intersection_scale)) {
                                if (!activeAxis.isZero()) {
                                    Vector3 delta = new Vector3(intersection_scale).sub(initialPosition);

                                    float axisDelta = delta.dot(activeAxis);

                                    Vector3 scaleVec = getVector3(axisDelta);

                                    selectedBound.setSize(scaleVec);
                                } else {
                                    float scaleFactor = intersection_scale.dst(initialPosition);
                                    selectedBound.setSize(new Vector3(scaleFactor,scaleFactor,scaleFactor));
                                }
                            }

                            break;
                        case ROTATE:

                            Quaternion q = new Quaternion();

                            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                                q.set(Vector3.X, -Gdx.input.getDeltaY() * 0.75f);
                                selectedBound.rotation.mulLeft(q);
                            } else {
                                q.set(Vector3.Y, -Gdx.input.getDeltaX() * 0.75f);
                                selectedBound.rotation.mulLeft(q);
                            }

                            selectedBound.rotation.nor();
                            selectedBound.rebuild();
                            break;
                    }
                }

                boundsLister.buttonHovered = cursor.clicked(boundsLister.buttonRect);

                if (cursor.clicked(boundsLister.box)) {
                    if (!boundsLister.dropDownExpand && boundsLister.buttonHovered)
                        boundsLister.dropDownExpand = true;
                } else if (boundsLister.dropDownExpand) {
                    boundsLister.dropDownExpand = false;
                }
            }

            for (PavLayout layout : boundsEditorLayout) {
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

        private Vector3 getVector3(float axisDelta) {
            float scaleFactor = 1f + axisDelta * 0.5f;  // 0.5f is sensitivity

            // Apply scaling uniformly or per-axis
            Vector3 scaleVec = new Vector3(1, 1, 1);
            if (activeAxis.equals(Vector3.X))
                scaleVec.set(scaleFactor, 1, 1);
            else if (activeAxis.equals(Vector3.Y))
                scaleVec.set(1, scaleFactor, 1);
            else if (activeAxis.equals(Vector3.Z))
                scaleVec.set(1, 1, scaleFactor);
            else
                scaleVec.set(scaleFactor, scaleFactor, scaleFactor); // free scaling
            return scaleVec;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {

            if (cursor.clicked(boundsLister.box) && !boundsLister.dropDownExpand) {
                boundsLister.scrollOffset += amountY * 10;
                return true;
            }

            for (PavLayout layout : boundsEditorLayout) {
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
            }

            pavCamera.zoom(amountY);


            return false;
        }
    };

    private static void setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior value) {
        PavEngine.editorSelectedObjectBehavior = value;
        editorSelectedObjectText.text = PavEngine.editorSelectedObjectBehavior.name();
    }

    private static void setSelectedObject(GameObject obj) {
        selectedObject = obj;
    }

    private static void setPerspectiveTouch() {

        Vector3 screenPos = overlayViewport.project(new Vector3(
            PavCursor.clickArea.getX() + PavCursor.clickArea.getWidth() / 2f,
            PavCursor.clickArea.getY() + PavCursor.clickArea.getHeight() / 2f,
            0
        ));

        screenPos.y = Gdx.graphics.getHeight() - screenPos.y;

        perspectiveTouchRay.set(camera.getPickRay(screenPos.x, screenPos.y));
    }

}
