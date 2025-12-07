package com.pavengine.app.PavScreen;

import static com.pavengine.app.Debug.Draw.debugCube;
import static com.pavengine.app.Debug.Draw.debugLine;
import static com.pavengine.app.Debug.Draw.debugRay;
import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.addAndGet;
import static com.pavengine.app.Methods.addObjects;
import static com.pavengine.app.Methods.listFile;
import static com.pavengine.app.Methods.listFolder;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavCamera.PavCamera.camera;
import static com.pavengine.app.PavCrypt.PavCrypt.readArray;
import static com.pavengine.app.PavEngine.axisGizmo;
import static com.pavengine.app.PavEngine.centerReferenceOriginRays;
import static com.pavengine.app.PavEngine.editorSelectedObjectText;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.hoverUIBG;
import static com.pavengine.app.PavEngine.overlayCamera;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.pavCamera;
//import static com.pavengine.app.PavEngine.perspectiveAxisGizmo;
import static com.pavengine.app.PavEngine.referenceEditorRays;
import static com.pavengine.app.PavEngine.sceneManager;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavInput.MapEditorInput.mapEditorInput;
import static com.pavengine.app.PavScreen.GameScreen.selectedObject;
import static com.pavengine.app.PavScreen.GameWorld.staticObjects;
import static com.pavengine.app.PavUI.PavAnchor.CENTER_LEFT;
import static com.pavengine.app.PavUI.PavAnchor.TOP_CENTER;
import static com.pavengine.app.PavUI.PavAnchor.TOP_RIGHT;
import static com.pavengine.app.PavUI.PavFlex.COLUMN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.ObjectType;
import com.pavengine.app.PavBounds.PavBounds;
import com.pavengine.app.PavCrypt.CryptSchema;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.DropDown;
import com.pavengine.app.PavUI.InputTag;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;
import com.pavengine.app.PavUI.TextButton;
import com.pavengine.app.ReferenceEditorLine;
import com.pavengine.app.ReferenceOriginLine;
import com.pavengine.app.StringBind;

import java.util.ArrayList;

public class MapEditor extends  PavScreen {
    public static ArrayList<String> objectList;
    public static Array<PavLayout> mapEditingLayout = new Array<>();
    public static PavWidget exportModelInfo;
    private BitmapFont font;
    public static String sceneName = "panelName";

    public MapEditor(PavEngine game) {
        super(game);

        this.font = gameFont[2];
        objectList = new ArrayList<>();


        mapEditingLayout.add(new PavLayout(CENTER_LEFT, COLUMN, 5, 226, 64, 5));
        mapEditingLayout.peek().addSprite(new DropDown(listFile("assets/scenes/"),new StringBind() {
            @Override
            public String get() {
                return sceneName;
            }

            @Override
            public void set(String value) {
                sceneName = value;
                readArray("assets/scenes/" + value +".bin", CryptSchema.GameObject, data -> {
                    String name = (String) data.get("field0");
                    ObjectType type = ObjectType.valueOf((String) data.get("field1"));
                    Vector3 position = (Vector3) data.get("field2");
                    Quaternion rotation = (Quaternion) data.get("field3");
                    Vector3 scale = (Vector3) data.get("field4");

                    addObjects(name, "STATIC", type, position, scale, rotation);
                });
            }
        }, mapEditorInput ,gameFont[2], hoverUIBG[2], uiBG[1]));

        for (String model : listFolder("assets/models/"))
            mapEditingLayout.peek().addSprite(new TextButton(model, font,hoverUIBG[2], uiBG[1], ClickBehavior.AddStaticObjectToMapEditor));

        addAndGet(mapEditingLayout,new PavLayout(TOP_RIGHT, COLUMN, 20, 224, 48, 8)).addSprite(new InputTag( new StringBind() {
            @Override public String get() { return sceneName; }
            @Override public void set(String value) { sceneName = value; }
        }, font, hoverUIBG[2], uiBG[0], ClickBehavior.Nothing)).addSprite(new TextButton("Export", font, hoverUIBG[3], uiBG[2], ClickBehavior.ExportModelInfo));

        PavEngine.editorSelectedObjectText = new TextButton("Free Move", font, ClickBehavior.Nothing);

        addAndGet(mapEditingLayout,new PavLayout(TOP_CENTER, COLUMN, 5, 192, 48, 5)).addSprite(editorSelectedObjectText);

    }




    @Override
    public void setInput() {
        Gdx.input.setInputProcessor(mapEditorInput);
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


        for (PavLayout layout : mapEditingLayout) {
            layout.draw(batch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());
        }


        batch.end();


//        for(PavLayout layout : mapEditingLayout) {
//            for(PavWidget widget : layout.widgets) {
//                if(widget.isHovered)
////                    debugRectangle(widget.box, Color.GREEN);
//            }
//        }


        for(GameObject obj : staticObjects) {
            if(obj == selectedObject)
                for(PavBounds box : obj.boxes)
                    debugCube(box, obj.debugColor);
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
