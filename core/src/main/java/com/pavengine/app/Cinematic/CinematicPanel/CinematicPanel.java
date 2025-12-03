package com.pavengine.app.Cinematic.CinematicPanel;

import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.resolution;
import static com.pavengine.app.PavEngine.uiBG;
import static com.pavengine.app.PavScreen.CinematicEditor.cinematicPanel;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.Dropdowns.CinematicPanelDropdown;
import com.pavengine.app.Dropdowns.Dropdown;

public class CinematicPanel {
    public CinematicPanelSelectedWidget selectedWidget = new CinematicPanelSelectedWidget();
    TextureRegion bg;
    public Rectangle bounds;
    float scrollY = resolution.y - 142;
    public Array<CinematicPanelWidget> panelWidgets = new Array<>();
    public Dropdown dropdown;
    public CinematicWidgetType currentWidgetType = CinematicWidgetType.Subtitle;
    public boolean widgetDrag = false;

    public CinematicPanel(TextureRegion bg) {
        this.bg = bg;
        this.bounds = new Rectangle(0,resolution.y/2.5f,284,resolution.y/0.6f);
        dropdown = new CinematicPanelDropdown(uiBG[1],uiBG[6], new Vector2(50f,resolution.y - 68));
        panelWidgets.add(new SubtitlePanelWidget("Subtitle", gameFont[2]));
        panelWidgets.add(new CameraPanelWidget("Camera", gameFont[2]));
    }

    public void draw(SpriteBatch batch) {
        batch.draw(bg,bounds.x,bounds.y,bounds.width,bounds.height);

        for(CinematicPanelWidget panelWidget : panelWidgets) {
            if(panelWidget.type == currentWidgetType) panelWidget.draw(batch, scrollY);
        }

        dropdown.draw(batch);

        if(cinematicPanel.widgetDrag) {
            cinematicPanel.selectedWidget.draw( batch, cursor.getPosition());

        }
    }

}
