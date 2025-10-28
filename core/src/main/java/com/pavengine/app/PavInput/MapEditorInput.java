package com.pavengine.app.PavInput;

import static com.pavengine.app.PavEngine.cursor;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

public class MapEditorInput {
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
            return false;
        }

        @Override
        public boolean keyTyped(char character) {
            return false;
        }

        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchUp(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
            return false;
        }

        @Override
        public boolean touchDragged(int screenX, int screenY, int pointer) {
            return false;
        }

        @Override
        public boolean mouseMoved(int screenX, int screenY) {
            return false;
        }

        @Override
        public boolean scrolled(float amountX, float amountY) {
            return false;
        }
    };
    }
