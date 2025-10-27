package com.pavengine.app.PavScreen;

import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.hoverUIBG;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavEngine.uiControl;
import static com.pavengine.app.PavUI.PavAnchor.CENTER_LEFT;
import static com.pavengine.app.PavUI.PavAnchor.TOP_RIGHT;
import static com.pavengine.app.PavUI.PavFlex.COLUMN;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavGameObject.GameObject;
import com.pavengine.app.PavUI.Checkbox;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.Dropdown;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;
import com.pavengine.app.PavUI.Stepper;
import com.pavengine.app.PavUI.TextButton;

import java.util.ArrayList;

public class MapEditor extends  PavScreen{
    public static Array<GameObject> staticMapObjects;
    public static ArrayList<String> objectList;
    public static ArrayList<PavLayout> mapEditingLayout = new ArrayList<>();
    public static Stepper scaleStepper, elevationStepper;
    public static Checkbox roomCheckbox;
    public static Dropdown selectedObjectType;
    public static ArrayList<Stepper> rotationSteppers = new ArrayList<>();
    public static PavWidget exportModelInfo;
    public Vector3[] rotationOffset = new Vector3[]{new Vector3(1, 0, 0), new Vector3(0, 1, 0), new Vector3(0, 0, 1)};
    public String[] rotationNames = new String[]{"Rotation Yaw", "Rotation Roll", "Rotation Pitch"};
    private BitmapFont font;
    

    public MapEditor(PavEngine game) {
        super(game);

        this.font = gameFont;
        staticMapObjects = new Array<>();
        objectList = new ArrayList<>();

        mapEditingLayout.add(new PavLayout(CENTER_LEFT, COLUMN, 5, 192, 128, 5));
        for (String model : listModels("assets/models/"))
            mapEditingLayout.get(0).addSprite(new TextButton(model, font, hoverUIBG[1], uiBG[1], ClickBehavior.AddStaticObjectToMapEditor));


        scaleStepper = new Stepper(192 + 32, 140 - 20, new Vector3(0.005f, 0.005f, 0.005f), ClickBehavior.StepperScale, "Scale", font, uiControl[0], uiControl[1]);
        elevationStepper = new Stepper(192 * 2 + 32, 140 - 20, new Vector3(0f, 0.05f, 0f), ClickBehavior.StepperElevation, "Elevation", font, uiControl[0], uiControl[1]);
        roomCheckbox = new Checkbox(192 * 3 + 32, 140 - 20, false, ClickBehavior.CheckboxRoom, "Room", font, uiControl[4], uiControl[5]);
        selectedObjectType = new Dropdown(192 + 32, 200, new String[]{"StaticObject", "TargetObject", "GroundObject", "KinematicObject"}, 1, font);
        mapEditingLayout.add(new PavLayout(TOP_RIGHT, COLUMN, 5, 192, 48, 5));
        mapEditingLayout.get(1).addSprite(new TextButton("Export", font, hoverUIBG[3], uiBG[2], ClickBehavior.ExportModelInfo));


        int i = 0;
        for (Vector3 offset : rotationOffset) {
            rotationSteppers.add(new Stepper(192 * (i + 1) + 32, 50 - 20, offset, ClickBehavior.StepperRotation, rotationNames[i], font, uiControl[0], uiControl[1]));
            i++;
        }

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
    public void debug() {

    }

    @Override
    public void draw(float delta) {

    }

    @Override
    public void world(float delta) {

    }
}
