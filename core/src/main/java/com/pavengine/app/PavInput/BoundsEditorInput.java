package com.pavengine.app.PavInput;

import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.axisGizmo;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.editorSelectedObjectBehavior;
import static com.pavengine.app.PavEngine.editorSelectedObjectText;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.pavCamera;
import static com.pavengine.app.PavEngine.perspectiveTouchRay;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavScreen.BoundsEditor.bounds;
import static com.pavengine.app.PavScreen.BoundsEditor.boundsEditorLayout;
import static com.pavengine.app.PavScreen.BoundsEditor.boundsLister;
import static com.pavengine.app.PavScreen.BoundsEditor.selectedBound;
import static com.pavengine.app.PavScreen.GameScreen.mapEditorPanel;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameScreen.world;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;

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
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavCursor;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavIntersector;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;

public class BoundsEditorInput {
    enum TransformMode { NONE, MOVE, SCALE, ROTATE }

    public static InputProcessor boundsEditorInput = new InputProcessor() {
        private TransformMode transformMode = TransformMode.NONE;
        private Vector3 activeAxis = Vector3.X;
        Plane dragPlane = new Plane();
        Vector3 dragOffset = new Vector3();
        Vector3 perspectiveTouch = new Vector3(), overlayTouch = new Vector3();
        Quaternion initialRotation = new Quaternion();
        float initialSize = 1;
        Vector3 initialPosition = new Vector3(0,0,0);

        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.G: // Move
                    transformMode = TransformMode.MOVE;
                    print("Mode: MOVE");
                    break;
                case Input.Keys.S: // Scale
                    transformMode = TransformMode.SCALE;
                    print("Mode: SCALE");
                    break;
                case Input.Keys.R: // Rotate
                    transformMode = TransformMode.ROTATE;
                    print("Mode: ROTATE");
                    break;
                case Input.Keys.X:
                    activeAxis = Vector3.X;
                    print("Axis: X");
                    break;
                case Input.Keys.Y:
                    activeAxis = Vector3.Y;
                    print("Axis: Y");
                    break;
                case Input.Keys.Z:
                    activeAxis = Vector3.Z;
                    print("Axis: Z");
                    break;
                case Input.Keys.ESCAPE:
                    transformMode = TransformMode.NONE;
                    activeAxis = Vector3.Zero;
                    print("Mode: NONE");
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

            setPerspectiveTouch();

            if(button == Input.Buttons.LEFT) {
                for (PavBounds obj : bounds) {
                    if (Intersector.intersectRayOrientedBounds(perspectiveTouchRay, obj.box, perspectiveTouch)) {
                        selectedBound = obj;
                        Vector3 center = obj.box.getBounds().getCenter(new Vector3());
                        dragPlane = new Plane(camera.direction, center);
                        dragOffset.set(new Vector3());
                        return true;
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

            setPerspectiveTouch();

            if(editorSelectedObjectBehavior != EditorSelectedObjectBehavior.FreeLook) {
                setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
            }


            if (button == Input.Buttons.LEFT) {

                if (selectedBound != null)
                    if (!Intersector.intersectRayOrientedBounds(perspectiveTouchRay, selectedBound.box, perspectiveTouch)) {
                        selectedBound = null;
                    }

                if (cursor.clicked(axisGizmo.xRect)) {
                    axisGizmo.lookFromAxis(Vector3.X);
                    return true;
                } else if (cursor.clicked(axisGizmo.yRect)) {
                    axisGizmo.lookFromAxis(new Vector3(0,-0.8f,0.1f));
                    return true;
                } else if (cursor.clicked(axisGizmo.zRect)) {
                    axisGizmo.lookFromAxis(Vector3.Z);
                    return true;
                }

                if (cursor.clicked(mapEditorPanel))
                    return true;

                for (PavLayout layout : boundsEditorLayout) {


                    for (PavWidget widget : layout.widgets) {

                        if (cursor.clicked(widget.box)) {

                            switch (widget.clickBehavior) {

                                case AddStaticObjectToMapEditor: {
                                    print("add : " + widget.text);
                                    world.addObject(widget.text, widget.text, new Vector3(0, 0, 0), 1, 10, 1, ObjectType.STATIC, new String[]{""});
                                    setSelectedObject(staticObjects.get(staticObjects.size - 1));
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

                    Vector3 intersection = new Vector3();
                    if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, intersection)) {
                        Vector3 newPos = intersection.add(dragOffset);
                        selectedBound.set(newPos);
                    }
                }
            }

            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {

            setPerspectiveTouch();

            if (selectedObject != null) {
                switch(editorSelectedObjectBehavior) {

                    case FreeLook:
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

        @Override
        public boolean scrolled(float amountX, float amountY) {

            if(cursor.clicked(boundsLister.box) && !boundsLister.dropDownExpand) {
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
