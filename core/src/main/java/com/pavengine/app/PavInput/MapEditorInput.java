package com.pavengine.app.PavInput;

  import static com.pavengine.app.Debug.Draw.debugLine;
  import static com.pavengine.app.Debug.Draw.debugRay;
  import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
  import static com.pavengine.app.PavEngine.axisGizmo;
  import static com.pavengine.app.PavEngine.cursor;
  import static com.pavengine.app.PavEngine.editorSelectedObjectBehavior;
  import static com.pavengine.app.PavEngine.editorSelectedObjectText;
  import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.overlayViewport;
  import static com.pavengine.app.PavEngine.perspectiveTouchRay;
  import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavScreen.GameScreen.mapEditorPanel;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameScreen.world;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;
import static com.pavengine.app.PavScreen.MapEditor.mapEditingLayout;


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

public class MapEditorInput {
    public static InputProcessor mapEditorInput = new InputProcessor() {
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

            if(keycode == Input.Keys.G) {
                setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.Grab);
                if(selectedObject != null) {
                    initialRotation = selectedObject.rotation.cpy();
                    initialPosition = selectedObject.pos;
                    initialSize = selectedObject.size.x;
                    dragPlane = new Plane(camera.direction, selectedObject.pos);
                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
                }
            }

            if(keycode == Input.Keys.R) {

                setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.Rotate);
                if(selectedObject != null) {
                    initialRotation = selectedObject.rotation.cpy();
                    initialPosition = selectedObject.pos;
                    initialSize = selectedObject.size.x;
                    dragPlane = new Plane(camera.direction, selectedObject.pos);
                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
                }
            }

            if(keycode == Input.Keys.Z) {
                setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.Scale);
                if(selectedObject != null) {
                    initialRotation = selectedObject.rotation.cpy();
                    initialPosition = selectedObject.pos;
                    initialSize = selectedObject.size.x;
                    dragPlane = new Plane(camera.direction, selectedObject.pos);
                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
                }
            }

            if(keycode == Input.Keys.X) {
                if (editorSelectedObjectBehavior != EditorSelectedObjectBehavior.FreeLook) {
                    if(selectedObject != null) {
                        selectedObject.rotation = initialRotation;
                        selectedObject.size = new Vector3(initialSize,initialSize,initialSize);
                        selectedObject.pos = initialPosition;
                    }
                }
                setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
            }

            if (keycode == Input.Keys.ESCAPE) {
                Gdx.input.setCursorCatched(!enableCursor);
                lockCursor(enableCursor);
            }

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

            setPerspectiveTouch();

//            if(selectedObject!=null) for(AxisGizmo3D.GizmoCube box : perspectiveAxisGizmo.boxes) {
//                if(PavIntersector.intersect( perspectiveTouchRay, box.box.getBounds(), box.box.transform, perspectiveTouch)) {
//
//                    print("gizmo drag");
//                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
//                    return true;
////                    Vector3 clickToCenter = new Vector3(selectedObject.pos).sub(perspectiveTouch);
////                    dragAxis.set(box.direction);
////                    axisOffset = clickToCenter.dot(dragAxis);
////
////                    dragPlane = new Plane(camera.direction, dragStartPos);
////                    gizmoDrag = true;
////                    return true;
//                }
//            }

            for (GameObject obj : staticObjects) {
                if (PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
                    setSelectedObject(obj);
                    dragPlane = new Plane(camera.direction, selectedObject.pos);
                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
                    return true;
                }
            }

            for (GameObject obj : targetObjects) {
                if (PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
                    setSelectedObject(obj);
                    dragOffset.set(selectedObject.pos).sub(perspectiveTouch);
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

            overlayTouch = new Vector3(screenX, screenY, 0);
            overlayViewport.unproject(overlayTouch);

            if (button == Input.Buttons.LEFT) {
                if (cursor.clicked(axisGizmo.xRect)) {
                    axisGizmo.lookFromAxis(Vector3.X);
                    return true;
                } else if (cursor.clicked(axisGizmo.yRect)) {
                    axisGizmo.lookFromAxis(new Vector3(0,0.8f,0.1f));
                    return true;
                } else if (cursor.clicked(axisGizmo.zRect)) {
                    axisGizmo.lookFromAxis(Vector3.Z);
                    return true;
                }

                if (cursor.clicked(mapEditorPanel))
                    return true;

                for (PavLayout layout : mapEditingLayout) {


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
                if (selectedObject != null)
                    if (!PavIntersector.intersect(perspectiveTouchRay, selectedObject.bounds, selectedObject.scene.modelInstance.transform, perspectiveTouch)) {
//                        print("deselect");
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


            overlayTouch = new Vector3(screenX, screenY, 0);
            overlayViewport.unproject(overlayTouch);


            if (enableCursor) cursor.setCursor(2);


//            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
//                if (selectedObject != null) {
//                    for (PavLayout layout : mapEditingLayout) {
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

            setPerspectiveTouch();

            if (selectedObject != null) {
                switch(editorSelectedObjectBehavior) {
                    case Grab:
                        Vector3 intersection = new Vector3();

                        if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, intersection)) {
                            selectedObject.pos.set(intersection.cpy().add(dragOffset));
                        }
                        break;

                    case Rotate:

                        float sensitivity = 1.5f;

                        Quaternion q = new Quaternion();

                        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                            q.set(Vector3.X, -Gdx.input.getDeltaY() * sensitivity);
                            selectedObject.rotation.mulLeft(q);
                        } else {
                            q.set(Vector3.Y, -Gdx.input.getDeltaX() * sensitivity);
                            selectedObject.rotation.mulLeft(q);
                        }

                        selectedObject.rotation.nor();

                        break;

                    case Scale:
                        Vector3 scaleIntersection = new Vector3();
                        if (Intersector.intersectRayPlane(perspectiveTouchRay, dragPlane, scaleIntersection)) {

                            float distance = scaleIntersection.dst(selectedObject.pos);

                            float minScale = 0.1f;

                            float scalePower = 0.3f;

                            float scaleFactor = minScale + (distance * scalePower);

                            scaleFactor = Math.min(scaleFactor, 50f);

                            selectedObject.size.set(new Vector3(scaleFactor,scaleFactor,scaleFactor));

                        }
                        break;
                    case FreeLook:
                        break;
                }
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
            PavCursor.clickArea.getX() + PavCursor.clickArea.getWidth() / 2f,
            PavCursor.clickArea.getY() + PavCursor.clickArea.getHeight() / 2f,
            0
        ));

        screenPos.y = Gdx.graphics.getHeight() - screenPos.y;

        perspectiveTouchRay.set(camera.getPickRay(screenPos.x, screenPos.y));
    }

}
