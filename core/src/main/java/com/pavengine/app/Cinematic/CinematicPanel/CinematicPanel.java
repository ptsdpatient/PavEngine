package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiBG;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class CinematicPanel {
    TextureRegion bg;
    Rectangle bounds;
    float scrollY = resolution.y - 72;
    Array<CinematicPanelWidget> panelWidgets = new Array<>();
    public CinematicPanelDropdown dropdown;
    public static CinematicWidgetType currentWidgetType = CinematicWidgetType.Camera;

    public CinematicPanel(TextureRegion bg) {
        this.bg = bg;
        this.bounds = new Rectangle(0,resolution.y/2.5f,284,resolution.y/0.6f);
//        panelWidgets.add(new CameraPanelWidget("Zoom", gameFont[2]));
        dropdown = new CinematicPanelDropdown(uiBG[1],uiBG[6], new Vector2(50f,resolution.y - 68  ));
    }

    public void draw(SpriteBatch batch) {
        batch.draw(bg,bounds.x,bounds.y,bounds.width,bounds.height);
        for(CinematicPanelWidget panelWidget : panelWidgets) {
            panelWidget.draw(batch, scrollY);
        }
        dropdown.draw(batch);
    }
}
