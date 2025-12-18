package com.pavengine.app.PavInput;

import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavCrypt.PavCrypt.readArray;
import static com.pavengine.app.PavCrypt.PavCrypt.writeArray;
import static com.pavengine.app.PavEngine.axisGizmo;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.editorSelectedObjectBehavior;
import static com.pavengine.app.PavEngine.editorSelectedObjectText;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.perspectiveTouchRay;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicEditorLayout;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicTimeline;
import static com.pavengine.app.PavScreen.CinematicEditor.playingScene;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameScreen.world;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;
import static com.pavengine.app.PavScreen.MapEditor.sceneName;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicPanelWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineControl;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.AnimateTimelineWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.CameraTimelineWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.CinematicTimelineWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.SoundTimelineWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.SubtitleTimelineWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.TransformTimelineWidget;
import com.pavengine.app.EditorSelectedObjectBehavior;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavBounds.PavBoundsType;
import com.pavengine.app.PavCrypt.CryptSchema;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavIntersector;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;

public class CinematicEditorInput {
    public static InputProcessor cinematicEditorInput = new InputProcessor() {
        private Vector3 activeAxis = Vector3.Zero;
        private TransformMode transformMode = TransformMode.NONE;

        Plane dragPlane = new Plane();
        Vector3 dragOffset = new Vector3();
        Vector3 perspectiveTouch = new Vector3();
        Vector3 initialPosition = new Vector3(0, 0, 0);
        Vector3 startPointerPos = new Vector3();
        Vector3 initialScale = new Vector3();
        Vector3 intersection_scale = new Vector3();
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

            setPerspectiveTouch();

            if (cursor.clicked(cinematicPanel.bounds)) {
                for (CinematicPanelWidget widget : cinematicPanel.panelWidgets) {
                    if (cursor.clicked(widget.bound)) {
                        cinematicPanel.widgetDrag = true;
                        cinematicPanel.selectedWidget.set(widget.texture, widget.name, new Vector2(widget.bound.x - cursor.clickArea.x, widget.bound.y - cursor.clickArea.y), widget.type);
                        return true;
                    }
                }
            }

            if (cursor.clicked(cinematicTimeline.bounds)) {
                for (CinematicTimelineWidget widget : cinematicTimeline.timelineWidgets) {
                    widget.offsetDrag.set(cursor.getX() - widget.bounds.getX(), cursor.getY() - widget.bounds.getY());
//                    print(widget.offsetDrag);
                }
            }

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

            for (PavLayout layout : cinematicEditorLayout) {


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
                                Array<PavBounds> boundsArray = new Array<>();
                                world.addObject(widget.text, widget.text, new Vector3(0, 0, 0), 1, ObjectType.STATIC);
                                readArray("assets/models/" + widget.text + "/bounds.bin" , CryptSchema.PavBounds, boundData -> {
                                    Vector3 boundPosition = (Vector3) boundData.get("field0");
                                    Vector3 boundScale = (Vector3) boundData.get("field1");
                                    Quaternion boundRotation = (Quaternion) boundData.get("field2");
                                    PavBoundsType boundType = PavBoundsType.valueOf( (String) boundData.get("field3"));
                                    boundsArray.add(new PavBounds(boundPosition, boundScale, boundRotation, boundType));
                                });
                                staticObjects.peek().boxes.addAll(boundsArray);
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
                for(PavBounds bound : obj.boxes) {
                    if(Intersector.intersectRayOrientedBoundsFast(perspectiveTouchRay, bound.box)) {
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
            }

            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {

            cursor.setCursor(1);

            if (cinematicPanel.widgetDrag) {
                cinematicPanel.widgetDrag = false;
                if (cinematicPanel.selectedWidget.snapping) {

                    CinematicTimelineWidget widget = null;

                    switch (cinematicPanel.selectedWidget.type) {
                        case Animate:
                            widget = new AnimateTimelineWidget();
                            break;
                        case Camera:
                            widget = new CameraTimelineWidget();
                            break;
                        case Sound:
                            widget = new SoundTimelineWidget();
                            break;
                        case Subtitle:
                            widget = new SubtitleTimelineWidget();
                            break;
                        case Transform:
                            widget = new TransformTimelineWidget();
                            break;
                    }

                    if (widget != null) {
                        cinematicTimeline.timelineWidgets.add(widget);
                    }
                }

            }


            if (!enableCursor) {
                lockCursor(false);
                Gdx.input.setCursorCatched(true);
            }

            setPerspectiveTouch();

            if (editorSelectedObjectBehavior != EditorSelectedObjectBehavior.FreeLook) {
                setEditorSelectedObjectBehavior(EditorSelectedObjectBehavior.FreeLook);
            }

            if (cursor.clicked(cinematicTimeline.bounds)) {
                for (CinematicTimelineControl control : cinematicTimeline.timelineControls) {
                    if (cursor.clicked(control.obj.getBoundingRectangle())) {
                        switch (control.index) {
                            case 0: {
                                cinematicTimeline.timeSeconds = 0;
                                cinematicTimeline.updateCursor();
                            }
                            break;
                            case 1: {
                                playingScene = !playingScene;
                            }
                            break;
                            case 2: {
                                cinematicTimeline.timeSeconds = 10;
                                cinematicTimeline.updateCursor();
                            }
                            break;
                        }
                    }
                }
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

                for (PavLayout layout : cinematicEditorLayout) {

                    for (PavWidget widget : layout.widgets) {

                        if (cursor.clicked(widget.box)) {

                            switch (widget.clickBehavior) {


                                case AddStaticObjectToMapEditor: {
                                    print("add : " + widget.text);
                                    world.addObject(widget.text, widget.text, new Vector3(0, 0, 0), 1, ObjectType.STATIC);
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

            if (enableCursor && cursor.index != 3) {
                cursor.setCursor(2);
                for (CinematicTimelineWidget widget : cinematicTimeline.timelineWidgets) {
                    if (cursor.clicked(widget.bounds)) {
                        widget.startTime = Math.max(((cursor.getX() - widget.offsetDrag.x - cinematicTimeline.scrollX) - cinematicTimeline.startX) / cinematicTimeline.pixelsPerSecond,0);
                    }
                }
            } else {
                float mouseX = cursor.getX();
                for (CinematicTimelineWidget widget : cinematicTimeline.timelineWidgets) {
                    if (cursor.clicked(widget.leftRectangle)) {
                        float newStartTime = (mouseX - cinematicTimeline.startX) / cinematicTimeline.pixelsPerSecond;

                        float delta = widget.startTime - newStartTime;

                        widget.startTime = newStartTime;
                        widget.duration += delta;

                        if (widget.duration < 1f) widget.duration = 1f;
                        break;
                    }

                    if (cursor.clicked(widget.rightRectangle)) {
                        float newEndTime = (mouseX - cinematicTimeline.startX) / cinematicTimeline.pixelsPerSecond;
                        widget.duration = newEndTime - widget.startTime;

                        if (widget.duration < 1f) widget.duration = 1f;
                        break;
                    }
                }
            }


            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {

            setPerspectiveTouch();


            if (cursor.clicked(cinematicTimeline.bounds)) {
                for (CinematicTimelineControl control : cinematicTimeline.timelineControls) {
                    control.hovered = cursor.clicked(control.obj.getBoundingRectangle());
                }
                boolean dragStarted = false;

                for (CinematicTimelineWidget widget : cinematicTimeline.timelineWidgets) {
                    if (cursor.clicked(widget.leftRectangle) || cursor.clicked(widget.rightRectangle)) {
                        dragStarted = true;
                        cursor.setCursor(3);
                        break;
                    }
                }
                if (!dragStarted) {
                    cursor.setCursor(1);
                }
            }

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


            for (GameObject obj : staticObjects) {
                boolean hit = PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch);
                obj.debugColor = hit ? Color.ORANGE : Color.YELLOW;
            }

            if (selectedObject != null) selectedObject.debugColor = Color.ORANGE;

            for (GameObject obj : targetObjects) {
                obj.debugColor = PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch) ? Color.ORANGE : Color.YELLOW;
            }

            for (PavLayout layout : cinematicEditorLayout) {
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

            if (cursor.clicked(cinematicTimeline.bounds)) {
                if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                    cinematicTimeline.resize(amountY);
                } else {
                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                        cinematicTimeline.updateScrolling(-amountY * 100, 0);
                    } else {
                        cinematicTimeline.updateScrolling(0, amountY * 100);
                    }
                }
                return true;
            }
            for (PavLayout layout : cinematicEditorLayout)
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
        editorSelectedObjectBehavior = value;
        editorSelectedObjectText.text = editorSelectedObjectBehavior.name();
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
