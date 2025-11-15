package com.pavengine.app.PavScreen;

import static com.pavengine.app.Debug.Draw.debugCube;
import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.addAndGet;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavEngine.axisGizmo;
import static com.pavengine.app.PavEngine.centerReferenceOriginRays;
import static com.pavengine.app.PavEngine.editorSelectedObjectText;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.hoverUIBG;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.pavCamera;
import static com.pavengine.app.PavEngine.referenceEditorRays;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavInput.BoundsEditorInput.boundsEditorInput;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavUI.PavAnchor.CENTER_LEFT;
import static com.pavengine.app.PavUI.PavAnchor.CENTER_RIGHT;
import static com.pavengine.app.PavUI.PavAnchor.TOP_CENTER;
import static com.pavengine.app.PavUI.PavAnchor.TOP_RIGHT;
import static com.pavengine.app.PavUI.PavFlex.COLUMN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavUI.BoundsLister;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;
import com.pavengine.app.PavUI.TextButton;
import com.pavengine.app.ReferenceEditorLine;
import com.pavengine.app.ReferenceOriginLine;

import java.util.ArrayList;

public class BoundsEditor extends  PavScreen {
    public static ArrayList<String> objectList;
    public static Array<PavLayout> boundsEditorLayout = new Array<>();
    public static PavWidget exportModelInfo;
    private BitmapFont font;
    public static BoundsLister boundsLister;
    public static Array<PavBounds> bounds = new Array<>();
    public static PavBounds selectedBound = new PavBounds();
    public BoundsEditor(PavEngine game) {
        super(game);

        this.font = gameFont;
        objectList = new ArrayList<>();


        boundsEditorLayout.add(new PavLayout(CENTER_LEFT, COLUMN, 5, 192, 64, 5));
        for (String model : listModels("assets/models/"))
            boundsEditorLayout.get(0).addSprite(new TextButton(model, font, hoverUIBG[2], uiBG[1], ClickBehavior.AddStaticObjectToMapEditor));


//        scaleStepper = new Stepper(192 + 32, 140 - 20, new Vector3(0.005f, 0.005f, 0.005f), ClickBehavior.StepperScale, "Scale", font, uiControl[0], uiControl[1]);
//        elevationStepper = new Stepper(192 * 2 + 32, 140 - 20, new Vector3(0f, 0.05f, 0f), ClickBehavior.StepperElevation, "Elevation", font, uiControl[0], uiControl[1]);
//        roomCheckbox = new Checkbox(192 * 3 + 32, 140 - 20, false, ClickBehavior.CheckboxRoom, "Room", font, uiControl[4], uiControl[5]);
//        selectedObjectType = new Dropdown(192 + 32, 200, new String[]{"StaticObject", "TargetObject", "GroundObject", "KinematicObject"}, 1, font);


        boundsLister = new BoundsLister(font, uiBG[1], hoverUIBG[2]);
        addAndGet(boundsEditorLayout,new PavLayout(TOP_RIGHT, COLUMN, 5, 192, 48, 5)).addSprite(new TextButton("Save", font, hoverUIBG[3], uiBG[2], ClickBehavior.ExportModelInfo));

        PavEngine.editorSelectedObjectText = new TextButton("Free Look", font, ClickBehavior.Nothing);

        addAndGet(boundsEditorLayout,new PavLayout(TOP_CENTER, COLUMN, 5, 192, 48, 5)).addSprite(editorSelectedObjectText);

        addAndGet(boundsEditorLayout,new PavLayout(CENTER_RIGHT, COLUMN, 5, 220, 350, 5)).addSprite(boundsLister);

    }

    public ArrayList<String> listModels(String path) {
        ArrayList<String> folders = new ArrayList<>();

        FileHandle dir = Gdx.files.internal(path);

        if (dir.exists()) {
            for (FileHandle file : dir.list()) {
                if (file.isDirectory()) {
                    folders.add(file.name());
                }
            }
        }
        return folders;
    }


    @Override
    public void setInput() {
        Gdx.input.setInputProcessor(boundsEditorInput);
    }


    @Override
    public void debug() {

    }

    @Override
    public void draw(float delta) {

    }

    @Override
    public void world(float delta) {

        Gdx.gl.glClearColor(0.15f, 0.15f, 0.15f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);
        Gdx.gl.glLineWidth(3f);

        Gdx.gl.glEnable(GL20.GL_CULL_FACE);
        Gdx.gl.glCullFace(GL20.GL_FRONT);
        Gdx.gl.glCullFace(GL20.GL_BACK);

        for(GameObject obj : staticObjects) {
            obj.update(delta);
        }


        pavCamera.update(delta);
        camera.update();
        overlayCamera.update();




        sceneManager.update(delta);
        sceneManager.render();


        for(ReferenceOriginLine or : centerReferenceOriginRays) {
            or.draw();
        }

        for(ReferenceEditorLine or : referenceEditorRays) {
            or.draw();
        }



        batch.setProjectionMatrix(overlayCamera.combined);
        batch.begin();


        for (PavLayout layout : boundsEditorLayout) {
            layout.draw(batch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());
        }


        batch.end();


//        for(PavLayout layout : boundsEditorLayout) {
//            for(PavWidget widget : layout.widgets) {
//                if(widget.isHovered)
//                    debugRectangle(widget.box, Color.GREEN);
//            }
//        }


        for(GameObject obj : staticObjects) {
            if(obj == selectedObject)
                for(PavBounds box : obj.boxes)
                    debugCube(box, obj.debugColor);
        }

        for(PavBounds box : bounds) {
            debugCube(box);
        }

        axisGizmo.update();
        axisGizmo.draw();

        if(selectedObject!=null) {
//            perspectiveAxisGizmo.update();
//            perspectiveAxisGizmo.draw();
        }


//        debugCube(new PavBounds(perspectiveAxisGizmo.xOBB),Color.RED);
//        debugCube(new PavBounds(perspectiveAxisGizmo.yOBB),Color.RED);
//
//        debugCube(new PavBounds(perspectiveAxisGizmo.zOBB),Color.RED);

//        debugRay(perspectiveTouchRay);

//        perspectiveAxisGizmo.handleInput();
//        print(camera.direction);

    }
}
