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
import static com.pavengine.app.PavScreen.BoundsEditor.boundsEditorLayout;
import static com.pavengine.app.PavScreen.BoundsEditor.boundsLister;
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
import com.pavengine.app.PavCursor;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavIntersector;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;

public class BoundsEditorInput {
    public static InputProcessor boundsEditorInput = new InputProcessor() {
        Plane dragPlane = new Plane();
        Vector3 dragOffset = new Vector3();
        Vector3 perspectiveTouch = new Vector3(), overlayTouch = new Vector3();
        Quaternion initialRotation = new Quaternion();
        float initialSize = 1;
        Vector3 initialPosition = new Vector3(0,0,0);

        @Override
        public boolean keyDown(int keycode) {
            return false;
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

//            if(selectedObject!=null) for(AxisGizmo3D.GizmoCube box : perspectiveAxisGizmo.boxes) {
//                if(PavIntersector.intersect( perspectiveTouchRay, box.box.getBounds(), box.box.transform, perspectiveTouch)) {
//
//                    print("gizmo drag");
//                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
//                    return true;
//                    Vector3 clickToCenter = new Vector3(selectedObject.pos).sub(perspectiveTouch);
//                    dragAxis.set(box.direction);
//                    axisOffset = clickToCenter.dot(dragAxis);
//
//                    dragPlane = new Plane(camera.direction, dragStartPos);
//                    gizmoDrag = true;
//                    return true;
//                }
//            }

//            for (GameObject obj : staticObjects) {
//                if (PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
//                    setSelectedObject(obj);
//                    dragPlane = new Plane(camera.direction, selectedObject.pos);
//                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
//                    return true;
//                }
//            }

//            for (GameObject obj : targetObjects) {
//                if (PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
//                    setSelectedObject(obj);
//                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
//                }
//            }

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

            overlayTouch = new Vector3(screenX, screenY, 0);
            overlayViewport.unproject(overlayTouch);

            if (button == Input.Buttons.LEFT) {

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
//                if (selectedObject != null)
//                    if (!PavIntersector.intersect(perspectiveTouchRay, selectedObject.bounds, selectedObject.scene.modelInstance.transform, perspectiveTouch)) {
////                        print("deselect");
//                        selectedObject.debugColor = Color.YELLOW;
//                        selectedObject = null;
//                    }
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


            overlayTouch = new Vector3(screenX, screenY, 0);
            overlayViewport.unproject(overlayTouch);


            if (enableCursor) cursor.setCursor(2);


//            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//                if (selectedObject != null) {
//                    for (PavLayout layout : boundsEditorLayout) {
//                        if (cursor.clicked(layout.box))
//                            return true;
//                    }
//
//                    if (cursor.clicked(mapEditorPanel))
//                        return true;
//
//                    if (PavIntersector.intersect(perspectiveTouchRay, selectedObject.bounds, selectedObject.scene.modelInstance.transform, perspectiveTouch))
//                        selectedObject.pos.set(perspectiveTouch.x, perspectiveTouch.y - selectedObject.getHeight()/2f, perspectiveTouch.z);
//                    Vector3 intersection = new Vector3();
//
//                    if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, intersection)) {
//                            selectedObject.pos.set(intersection.cpy().add(dragOffset));
//                    }
//                }
//            }

            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {

//            print("mouse moving");

            setPerspectiveTouch();

            if (selectedObject != null) {
                switch(editorSelectedObjectBehavior) {

                    case FreeLook:
                        break;
                }
            }

//            if(cursor.clicked(boundsLister.buttonRect)) {

            boundsLister.buttonHovered = cursor.clicked(boundsLister.buttonRect);

            if (cursor.clicked(boundsLister.box)) {
                if (!boundsLister.dropDownExpand && boundsLister.buttonHovered)
                    boundsLister.dropDownExpand = true;
            } else if (boundsLister.dropDownExpand) {
                boundsLister.dropDownExpand = false;
            }
//            }

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

//            print("scrolled");



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
            PavCursor.clickArea.getX() + PavCursor.clickArea.getWidth() / 2f,
            PavCursor.clickArea.getY() + PavCursor.clickArea.getHeight() / 2f,
            0
        ));

        screenPos.y = Gdx.graphics.getHeight() - screenPos.y;

        perspectiveTouchRay.set(camera.getPickRay(screenPos.x, screenPos.y));
    }

}
