package com.pavengine.app.PavInput;

import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.PavEngine.cameraBehavior;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.pavCamera;
import static com.pavengine.app.PavEngine.perspectiveViewport;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.mapEditingLayout;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.roomCheckbox;
import static com.pavengine.app.PavScreen.GameWorld.MapEditor.selectedObjectType;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.dragAndDrop;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.enableMapEditor;
import static com.pavengine.app.PavPlayer.PavPlayer.player;
import static com.pavengine.app.PavScreen.GameScreen.mapEditorPanel;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameScreen.world;
import static com.pavengine.app.PavScreen.GameWorld.pathFinder;
import static com.pavengine.app.PavScreen.GameWorld.sceneManager;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavIntersector;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;

public class GameInput {

    public static InputProcessor gameWorldInput = new InputProcessor() {
        public final float panSpeed = 0.1f;
        public int lastX = (int) cursor.cursor.getX(), lastY =  (int) cursor.cursor.getY();
        Ray ray = new Ray();
        Vector3 perspectiveTouch = new Vector3(), overlayTouch = new Vector3();
        Vector3 worldPos = new Vector3();
        boolean firstMove = true;

        @Override
        public boolean keyDown(int keycode) {

            return false;
        }

        @Override
        public boolean keyUp(int keycode) {
            if(keycode == Input.Keys.ESCAPE) {
                Gdx.input.setCursorCatched(!enableCursor);
                lockCursor(enableCursor);
            }

            if (keycode == Input.Keys.SPACE) {
                if (pathFinder.hasStart && pathFinder.hasEnd && !pathFinder.findingPath) {
                    pathFinder.findPath();
                }
            }

            if (keycode == Input.Keys.R) {
                pathFinder.reset();
            }

            if (enableMapEditor && dragAndDrop) {

                if (keycode == Input.Keys.DEL || keycode == Input.Keys.FORWARD_DEL) {
                    if (selectedObject != null) {
//                                print("deleted");
                        sceneManager.removeScene(selectedObject.scene);
                        staticObjects.removeValue(selectedObject, true);
                    }
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
            ray = perspectiveViewport.getPickRay(screenX, screenY);
            switch (cameraBehavior) {
                case TopDown: {
                    if (dragAndDrop && enableCursor) {
                        if (enableMapEditor) {
                            for (GameObject obj : staticObjects) {
                                if (PavIntersector.intersect(ray, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
                                    selectedObject = obj;
                                    roomCheckbox.value = selectedObject.isRoom;
                                    lastX = screenX;
                                    lastY = screenY;
                                    return true;
                                }
                            }
                        }
                        for (GameObject obj : targetObjects) {
                            if (PavIntersector.intersect(ray, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
                                selectedObject = obj;
                            }
                        }
                    }
                }
                ;
                break;
            }
            lastX = screenX;
            lastY = screenY;
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {

            if(!enableCursor) {
                lockCursor(false);
                Gdx.input.setCursorCatched(true);
            }
            switch (cameraBehavior) {
                case TopDown: {
                    if (dragAndDrop) {
                        ray = perspectiveViewport.getPickRay(screenX, screenY);

                        overlayTouch = new Vector3(screenX, screenY, 0);
                        overlayViewport.unproject(overlayTouch);

                        if (enableMapEditor) {
                            if (button == Input.Buttons.LEFT) {
//                                            for(PavLayout layout : mapEditingLayout) {
//                                                if (layout.box.contains(overlayTouch.x, overlayTouch.y))
//                                                    return true;
//                                            }
                                if (mapEditorPanel.contains(overlayTouch.x, overlayTouch.y))
                                    return true;

                                for (PavLayout layout : mapEditingLayout) {


                                    for (PavWidget widget : layout.widgets) {

                                        if (widget.box.contains(overlayTouch.x, overlayTouch.y)) {
//                                                    print(overlayTouch + " : " + widget.box);

                                            switch (widget.clickBehavior) {


                                                case AddStaticObjectToMapEditor: {
                                                    print("add : " + widget.text);
                                                    world.addObject(widget.text, widget.text, new Vector3(0, 0, 0), 1, 10, 1, ObjectType.STATIC, new String[]{""});
                                                    roomCheckbox.value = false;
                                                    selectedObjectType.selectedIndex = 0;

                                                    selectedObject = staticObjects.get(staticObjects.size - 1);
                                                    print(selectedObject == null ? "null" : "exists");
                                                    return true;
                                                }
                                                case ExitGame: {
                                                    Gdx.app.exit();
                                                }
                                                ;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (selectedObject != null)
                                    if (!PavIntersector.intersect(ray, selectedObject.bounds, selectedObject.scene.modelInstance.transform, perspectiveTouch)) {
                                        print("deselect");
                                        selectedObject.debugColor = Color.YELLOW;
                                        selectedObject = null;
                                    }
                            }
                        }


                        cursor.setCursor(1);
                    }
                }
                break;
            }
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            ray = perspectiveViewport.getPickRay(screenX, screenY);

            overlayTouch = new Vector3(screenX, screenY, 0);
            overlayViewport.unproject(overlayTouch);

            switch (cameraBehavior) {
                case Isometric:
                case TopDown: {
                    if (enableCursor) cursor.setCursor(2);

                    if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
                        pavCamera.pan(-(screenX - lastX) * panSpeed, (screenY - lastY) * panSpeed);
                    }

                    if (dragAndDrop) {
                        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                            if (selectedObject != null) {

                                for (PavLayout layout : mapEditingLayout) {
                                    if (layout.box.contains(overlayTouch.x, overlayTouch.y))
                                        return true;
                                }

                                if (mapEditorPanel.contains(overlayTouch.x, overlayTouch.y))
                                    return true;

                                worldPos = new Vector3(screenX, screenY, perspectiveViewport.project(new Vector3(selectedObject.pos)).z);
                                perspectiveViewport.unproject(worldPos);
                                selectedObject.pos.set(worldPos.x, selectedObject.pos.y, worldPos.z);
                            }
                        }
                    }
                    lastX = screenX;
                    lastY = screenY;
                }
                ;
                break;
                case FirstPerson:
                case ThirdPerson: {
                }
                break;
            }
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {

            if (enableCursor) {
                if (firstMove) {
                    lastX = screenX;
                    lastY = screenY;
                    firstMove = false;
                    return false;
                }

                Vector2 dir = new Vector2(screenX - lastX, screenY - lastY);
                cursor.move(dir);

                lastX = screenX;
                lastY = screenY;
            }

            ray = perspectiveViewport.getPickRay(screenX, screenY);
//            print(screenX + " : " + screenY);

            switch (cameraBehavior) {
                case TopDown: {

                    if (enableMapEditor) {

                        overlayTouch = new Vector3(screenX, screenY, 0);
                        overlayViewport.unproject(overlayTouch);

//                        print(overlayTouch.x + " : " + overlayTouch.y);

                        for (PavLayout layout : mapEditingLayout) {
                            layout.isHovered = layout.box.contains(overlayTouch.x, overlayTouch.y);
                            if (layout.isHovered) {
                                for (PavWidget widget : layout.widgets) {
                                    widget.isHovered = widget.box.contains(overlayTouch.x, overlayTouch.y);
                                }
                            } else {
                                for (PavWidget widget : layout.widgets) {
                                    if (widget.isHovered) {
                                        widget.isHovered = false;
                                    }
                                }
                            }
                        }
                    }
                    if (ray.direction.y != 0) {
                        float t = -ray.origin.y / ray.direction.y;
                        Vector3 intersection = ray.origin.cpy().add(ray.direction.cpy().scl(t));

                        // Compute horizontal direction from player to intersection
                        Vector3 lookDir = intersection.cpy().sub(player.pos);
                        lookDir.y = 0; // ignore vertical component
                        lookDir.nor();

                        // Compute yaw only
                        float yaw = (float) Math.toDegrees(Math.atan2(lookDir.x, lookDir.z));

                        // Apply yaw to player rotation
                        player.rotation.setEulerAngles(yaw, 0f, 0f); // yaw, pitch=0, roll=0
                    }

                    if (dragAndDrop) {
                        if (enableMapEditor) {
                            for (GameObject obj : staticObjects) {
                                obj.debugColor = PavIntersector.intersect(ray, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch) ? Color.CYAN : Color.YELLOW;
                            }
                            if (selectedObject != null) {
                                selectedObject.debugColor = Color.CYAN;
                            }
                        }
                        for (GameObject obj : targetObjects) {
                            obj.debugColor = PavIntersector.intersect(ray, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch) ? Color.CYAN : Color.YELLOW;
                        }
                    }
                }
                break;
                case FirstPerson:
                case ThirdPerson: {
                    pavCamera.rotate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
                }
                break;
            }
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            switch (cameraBehavior) {
                case Isometric:
                case TopDown: {
                    if (enableMapEditor) {
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
//                                            && (layout.leftX*-1 - layoutScrollAmount <= layout.renderWidth - 1000)
                                ) {
                                    layout.leftX += layoutScrollAmount;
                                    return false;
                                }

                            }
                    }
                    pavCamera.zoom(amountY);

                }
                ;
                break;
                case FirstPerson:
                case ThirdPerson: {
//                            pavCamera.rotate(-Gdx.input.getDeltaX(), Gdx.input.getDeltaY());
                }
                break;
            }
            return false;
        }
    };

}
