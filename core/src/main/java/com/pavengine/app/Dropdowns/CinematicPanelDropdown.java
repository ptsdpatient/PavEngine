package com.pavengine.app.Dropdowns;

import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class CinematicPanelDropdown extends Dropdown {
    CinematicWidgetType[] optionList = new CinematicWidgetType[]{
        CinematicWidgetType.Animate,
        CinematicWidgetType.Camera,
        CinematicWidgetType.Music,
        CinematicWidgetType.Subtitle
    };


    public CinematicPanelDropdown(TextureRegion background, TextureRegion hover, Vector2 position) {
        super(background, hover, position, new String[]{
            CinematicWidgetType.Animate.name(),
            CinematicWidgetType.Camera.name(),
            CinematicWidgetType.Music.name(),
            CinematicWidgetType.Subtitle.name()
        });
    }

    @Override
    void optionClicked(int i) {
//        cinematicPanel.currentWidgetType = optionList[i];
//        buttonLayout.setText(font, cinematicPanel.currentWidgetType.name());
    }
}
