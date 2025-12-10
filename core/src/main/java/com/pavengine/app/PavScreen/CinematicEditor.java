package com.pavengine.app.PavScreen;

import static com.pavengine.app.Debug.Draw.debugCube;
import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.addAndGet;
import static com.pavengine.app.Methods.addObjects;
import static com.pavengine.app.Methods.listFile;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavCrypt.PavCrypt.readArray;
import static com.pavengine.app.PavEngine.centerReferenceOriginRays;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.editorSelectedObjectText;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.hoverUIBG;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.pavCamera;
import static com.pavengine.app.PavEngine.referenceEditorRays;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavEngine.subtitle;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavEngine.uiControl;
import static com.pavengine.app.PavInput.CinematicEditorInput.cinematicEditorInput;
import static com.pavengine.app.PavInput.MapEditorInput.mapEditorInput;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavUI.PavAnchor.CENTER_LEFT;
import static com.pavengine.app.PavUI.PavAnchor.TOP_CENTER;
import static com.pavengine.app.PavUI.PavAnchor.TOP_RIGHT;
import static com.pavengine.app.PavUI.PavFlex.COLUMN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Cinematic.CinematicModal.CameraCinematicModal;
import com.pavengine.app.Cinematic.CinematicModal.CinematicModal;
import com.pavengine.app.Cinematic.CinematicModal.SubtitleCinematicModal;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicPanel;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicPanelWidget;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimeline;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineObject;
import com.pavengine.app.Cinematic.CinematicTimeline.CinematicTimelineWidget.CinematicTimelineWidget;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavBounds.PavBoundsType;
import com.pavengine.app.PavCrypt.CryptSchema;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.DropDown;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;
import com.pavengine.app.PavUI.TextButton;
import com.pavengine.app.ReferenceEditorLine;
import com.pavengine.app.ReferenceOriginLine;
import com.pavengine.app.StringBind;

import java.util.ArrayList;

public class CinematicEditor extends  PavScreen {

    public static ArrayList<String> objectList;
    public static Array<PavLayout> cinematicEditorLayout = new Array<>();
    public static PavWidget exportModelInfo;
    private BitmapFont font;
    public static CinematicTimeline cinematicTimeline;
    public static CinematicPanel cinematicPanel;
    public static boolean playingScene = false;
    public static CinematicModal cinematicModal;
    public static GlyphLayout cameraReferenceLayout = new GlyphLayout();
    public String sceneName = "scene";

    public CinematicEditor(PavEngine game) {
        super(game);

        this.font = gameFont[2];
        objectList = new ArrayList<>();


        cinematicEditorLayout.add(new PavLayout(CENTER_LEFT, COLUMN, 5, 192, 64, 5));
//        for (String model : listModels("assets/models/"))
//            cinematicEditorLayout.get(0).addSprite(new TextButton(model, font, hoverUIBG[1], uiBG[1], ClickBehavior.AddStaticObjectToMapEditor));


//        scaleStepper = new Stepper(192 + 32, 140 - 20, new Vector3(0.005f, 0.005f, 0.005f), ClickBehavior.StepperScale, "Scale", font, uiControl[0], uiControl[1]);
//        elevationStepper = new Stepper(192 * 2 + 32, 140 - 20, new Vector3(0f, 0.05f, 0f), ClickBehavior.StepperElevation, "Elevation", font, uiControl[0], uiControl[1]);
//        roomCheckbox = new Checkbox(192 * 3 + 32, 140 - 20, false, ClickBehavior.CheckboxRoom, "Room", font, uiControl[4], uiControl[5]);
//        selectedObjectType = new Dropdown(192 + 32, 200, new String[]{"StaticObject", "TargetObject", "GroundObject", "KinematicObject"}, 1, font);


        addAndGet(cinematicEditorLayout,new PavLayout(TOP_RIGHT, COLUMN, 5, 226, 48, 5)).addSprite(new TextButton("Export", font, hoverUIBG[3], uiBG[2], ClickBehavior.ExportModelInfo))
            .addSprite(new DropDown(listFile("assets/scenes/"), new StringBind() {
                @Override
                public String get() {
                    return sceneName;
                }

                @Override
                public void set(String value) {

                    sceneName = value;

                    readArray("assets/scenes/" + value + ".bin", CryptSchema.GameObject, data -> {
                        String name = (String) data.get("field0");
                        ObjectType type = ObjectType.valueOf((String) data.get("field1"));
                        Vector3 position = (Vector3) data.get("field2");
                        Quaternion rotation = (Quaternion) data.get("field3");
                        Vector3 scale = (Vector3) data.get("field4");
                        Array<PavBounds> objectBounds = new Array<>();

                        readArray("assets/models/" + name + "/bounds.bin" , CryptSchema.PavBounds, boundData -> {
                            Vector3 boundPosition = (Vector3) boundData.get("field0");
                            Vector3 boundScale = (Vector3) boundData.get("field1");
                            Quaternion boundRotation = (Quaternion) boundData.get("field2");
                            PavBoundsType boundType = PavBoundsType.valueOf( (String) boundData.get("field3"));
                            objectBounds.add(new PavBounds(boundPosition, boundScale, boundRotation, boundType));
                        });

                        addObjects(name, "STATIC", type, position, scale, rotation, objectBounds);
                    });
                }

            }, cinematicEditorInput, gameFont[2], hoverUIBG[2], uiBG[1]));

        PavEngine.editorSelectedObjectText = new TextButton("Free Move", font, ClickBehavior.Nothing);

        addAndGet(cinematicEditorLayout,new PavLayout(TOP_CENTER, COLUMN, 5, 192, 48, 5)).addSprite(editorSelectedObjectText);

        cinematicTimeline = new CinematicTimeline(uiBG[1], uiBG[7]);
        cinematicPanel = new CinematicPanel(uiBG[1]);

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
        Gdx.input.setInputProcessor(cinematicEditorInput);
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

        for (PavLayout layout : cinematicEditorLayout) {
            layout.draw(batch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());
        }

        cinematicTimeline.draw(batch);
        cinematicPanel.draw(batch);

        if(cinematicModal != null){
            cinematicModal.render(batch);
            if(cursor.clicked(cinematicModal.closeBound) && Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                cinematicModal.save();
                print("not cinematic modal anymore");
                Gdx.input.setInputProcessor(cinematicEditorInput);
                cinematicModal = null;
            }

        }
        if((Gdx.input.getInputProcessor() == cinematicEditorInput)) {
            gameFont[1].draw(batch, cameraReferenceLayout,resolution.x - cameraReferenceLayout.width,resolution.y / 2.5f + cameraReferenceLayout.height + 16);
        }

        batch.end();


        for(PavLayout layout : cinematicEditorLayout) {
            for(PavWidget widget : layout.widgets) {
//                if(widget.isHovered)
//                    debugRectangle(widget.box, Color.GREEN);
            }
        }


        for(GameObject obj : staticObjects) {
            if(obj == selectedObject)
                for(PavBounds box : obj.boxes)
                    debugCube(box, obj.debugColor);
        }

        for(CinematicPanelWidget widget : cinematicPanel.panelWidgets) {
//            debugRectangle(widget.bound, Color.GREEN);

        }

//        axisGizmo.update();
//        axisGizmo.draw();

        if(selectedObject!=null) {
//            perspectiveAxisGizmo.update();
//            perspectiveAxisGizmo.draw();
        }
        for(CinematicTimelineObject object : cinematicTimeline.timelineObjects) {
//            debugRectangle(object.lineRect,Color.YELLOW);
        }

//        for(CinematicPanelWidget widget : cinematicPanel.panelWidgets) {
//
//        }

//        debugRectangle(cinematicPanel.selectedWidget.lineRect,Color.CYAN);

        for (CinematicTimelineWidget widget : cinematicTimeline.timelineWidgets) {
//            debugRectangle(widget.leftRectangle,Color.RED);
//            debugRectangle(widget.rightRectangle,Color.RED);
//            debugRectangle(widget.bounds,Color.BLUE);
        }


//        debugRectangle(cursor.clickArea,Color.GREEN);


//        debugCube(new PavBounds(perspectiveAxisGizmo.xOBB),Color.RED);
//        debugCube(new PavBounds(perspectiveAxisGizmo.yOBB),Color.RED);
//
//        debugCube(new PavBounds(perspectiveAxisGizmo.zOBB),Color.RED);

//        debugRay(perspectiveTouchRay);

//        perspectiveAxisGizmo.handleInput();
//        print(camera.direction);
        if(cinematicModal!=null) {
            for(Rectangle rect : cinematicModal.getDebugRect()) {
//                debugRectangle(rect,Color.BLUE);
            }
            cinematicModal.debug(batch);
        }


    }


}
