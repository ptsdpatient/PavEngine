package com.pavengine.app.PavInput;

import static com.pavengine.app.Debug.Draw.debugLine;
import static com.pavengine.app.Debug.Draw.debugRay;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.enableCursor;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.perspectiveTouchRay;
import static com.pavengine.app.PavEngine.perspectiveViewport;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavScreen.GameScreen.mapEditorPanel;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameScreen.world;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavScreen.GameWorld.targetObjects;
import static com.pavengine.app.PavScreen.MapEditor.mapEditingLayout;
import static com.pavengine.app.PavScreen.MapEditor.roomCheckbox;
import static com.pavengine.app.PavScreen.MapEditor.selectedObjectType;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavIntersector;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;

public class MapEditorInput {
    public static InputProcessor mapEditorInput = new InputProcessor() {
        public final float panSpeed = 0.1f;
        public int lastX = (int) cursor.cursor.getX(),
            lastY = (int) cursor.cursor.getY();
        Vector3 perspectiveTouch = new Vector3(), overlayTouch = new Vector3();

        Vector3 worldPos = new Vector3();
        boolean firstMove = true;

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

//            if (keycode == Input.Keys.SPACE) {
//                if (pathFinder.hasStart && pathFinder.hasEnd && !pathFinder.findingPath) {
//                    pathFinder.findPath();
//                }
//            }

//            if (keycode == Input.Keys.R) {
//                pathFinder.reset();
//            }


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
            perspectiveTouchRay = perspectiveViewport.getPickRay(screenX, screenY);

            for (GameObject obj : staticObjects) {
                if (PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
                    selectedObject = obj;
                    roomCheckbox.value = selectedObject.isRoom;
                    lastX = screenX;
                    lastY = screenY;
                    return true;
                }
            }

            for (GameObject obj : targetObjects) {
                if (PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch)) {
                    selectedObject = obj;
                }
            }
            lastX = screenX;
            lastY = screenY;
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {

            cursor.setCursor(1);

            if (!enableCursor) {

                lockCursor(false);
                Gdx.input.setCursorCatched(true);

            }

            perspectiveTouchRay = perspectiveViewport.getPickRay(screenX, screenY);

            overlayTouch = new Vector3(screenX, screenY, 0);
            overlayViewport.unproject(overlayTouch);

            if (button == Input.Buttons.LEFT) {

                if (cursor.clicked(mapEditorPanel))
                    return true;

                for (PavLayout layout : mapEditingLayout) {


                    for (PavWidget widget : layout.widgets) {

                        if (cursor.clicked(widget.box)) {
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
                    if (!PavIntersector.intersect(perspectiveTouchRay, selectedObject.bounds, selectedObject.scene.modelInstance.transform, perspectiveTouch)) {
                        print("deselect");
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

            perspectiveTouchRay = perspectiveViewport.getPickRay(screenX, screenY);

            debugRay(perspectiveTouchRay);

            overlayTouch = new Vector3(screenX, screenY, 0);
            overlayViewport.unproject(overlayTouch);


            if (enableCursor) cursor.setCursor(2);

//            if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
//                pavCamera.pan(-(screenX - lastX) * panSpeed, (screenY - lastY) * panSpeed);
//            }

            if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
                if (selectedObject != null) {
                    for (PavLayout layout : mapEditingLayout) {
                        if (cursor.clicked(layout.box))
                            return true;
                    }

                    if (cursor.clicked(mapEditorPanel))
                        return true;


                    Vector3 worldPos = new Vector3(screenX, Gdx.graphics.getHeight() - screenY, 0);
                    Vector3 projected = perspectiveViewport.project(new Vector3(selectedObject.pos));
                    worldPos.z = projected.z;

                    print(perspectiveViewport.getWorldWidth() + " , " + perspectiveViewport.getWorldHeight());

                    print(worldPos + " , " + projected);

                    perspectiveViewport.unproject(worldPos);

                    selectedObject.pos.set(worldPos.x, selectedObject.pos.y, worldPos.z);

                }
            }
            lastX = screenX;
            lastY = screenY;

            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {

            print(screenX + " : " + screenY);
            Vector3 overlayPos = new Vector3(
                cursor.cursor.getX() + cursor.cursor.getWidth() / 2f,
                cursor.cursor.getY() + cursor.cursor.getHeight() / 2f,
                0
            );

            Vector3 screenPos = overlayViewport.project(overlayPos);
            screenPos.y = Gdx.graphics.getHeight() - screenPos.y;

            perspectiveTouchRay = camera.getPickRay(screenPos.x, screenPos.y);



//            print(cursor.cursor.getX() + " : " + (cursor.cursor.getY() - Gdx.graphics.getHeight()));


            for (GameObject obj : staticObjects) {
                boolean hit = PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch);
                obj.debugColor = hit ? Color.CYAN : Color.YELLOW;
                if (hit) System.out.println("Hit object: " + obj.name);
            }

            // 6️⃣ Highlight selected
            if (selectedObject != null) selectedObject.debugColor = Color.CYAN;



            for (GameObject obj : targetObjects) {
                obj.debugColor = PavIntersector.intersect(perspectiveTouchRay, obj.bounds, obj.scene.modelInstance.transform, perspectiveTouch) ? Color.CYAN : Color.YELLOW;
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
//                                            && (layout.leftX*-1 - layoutScrollAmount <= layout.renderWidth - 1000)
                    ) {
                        layout.leftX += layoutScrollAmount;
                        return false;
                    }

                }
//            pavCamera.zoom(amountY);

            return false;
        }
    };
}
