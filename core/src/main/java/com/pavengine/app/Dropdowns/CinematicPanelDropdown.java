package com.pavengine.app.Dropdowns;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.pavengine.app.Cinematic.CinematicPanel.CinematicWidgetType;

public class CinematicPanelDropdown extends Dropdown {
    CinematicWidgetType[] optionList = new CinematicWidgetType[]{
        CinematicWidgetType.Animate,
        CinematicWidgetType.Camera,
        CinematicWidgetType.Sound,
        CinematicWidgetType.Subtitle
    };


    public CinematicPanelDropdown(TextureRegion background, TextureRegion hover, Vector2 position) {
        super(background,196, hover, position, new String[]{
            CinematicWidgetType.Animate.name(),
            CinematicWidgetType.Camera.name(),
            CinematicWidgetType.Sound.name(),
            CinematicWidgetType.Subtitle.name()
        });
    }

    @Override
    void optionClicked(int i) {
//        cinematicPanel.currentWidgetType = optionList[i];
//        buttonLayout.setText(font, cinematicPanel.currentWidgetType.name());
    }
}
