package com.pavengine.app.Dropdowns;

import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicModal;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.CameraTransitionMode;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class CinematicCameraModalDropdown extends Dropdown {

    public CameraTransitionMode currentMode;

    CameraTransitionMode[] transitionModes = new CameraTransitionMode[] {
        CameraTransitionMode.LINEAR,
        CameraTransitionMode.SMOOTHSTEP,
        CameraTransitionMode.EASE_IN,
        CameraTransitionMode.EASE_OUT,
        CameraTransitionMode.EASE_IN_OUT,
        CameraTransitionMode.BEZIER_Y,
        CameraTransitionMode.BEZIER_XZ,
        CameraTransitionMode.AXIS_PRIORITY,
        CameraTransitionMode.STAGGERED
    };


    public CinematicCameraModalDropdown(TextureRegion background, TextureRegion hover, Vector2 position, CameraTransitionMode mode) {
        super(background,196, hover, position, new String[]{
            CameraTransitionMode.LINEAR.name(),
            CameraTransitionMode.SMOOTHSTEP.name(),
            CameraTransitionMode.EASE_IN.name(),
            CameraTransitionMode.EASE_OUT.name(),
            CameraTransitionMode.EASE_IN_OUT.name(),
            CameraTransitionMode.BEZIER_Y.name(),
            CameraTransitionMode.BEZIER_XZ.name(),
            CameraTransitionMode.AXIS_PRIORITY.name(),
            CameraTransitionMode.STAGGERED.name()
        });
        this.currentMode = mode;
        buttonLayout = new GlyphLayout(font, mode.name());
    }

    @Override
    void optionClicked(int i) {
        currentMode = transitionModes[i];
        buttonLayout.setText(font, currentMode.name());
        print(currentMode.name());
    }
}
