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
    float scrollY = 0;
    public Array<CinematicPanelWidget> panelWidgets = new Array<>();

    public boolean widgetDrag = false;

    public CinematicPanel(TextureRegion bg) {
        this.bg = bg;
        this.bounds = new Rectangle(0,resolution.y/2.5f,284,resolution.y/0.6f);

        panelWidgets.add(new SubtitlePanelWidget("Subtitle", new Vector2( 24,resolution.y - 30)));
        panelWidgets.add(new CameraPanelWidget("Camera", new Vector2(24,resolution.y - 30 - 48)));
        panelWidgets.add(new AnimatePanelWidget("Animate", new Vector2(24,resolution.y - 30 - 48*2)));
        panelWidgets.add(new SoundPanelWidget("Sound", new Vector2(24,resolution.y - 30 - 48*3)));
        panelWidgets.add(new TransformPanelWidget("Transform", new Vector2(24,resolution.y - 30 - 48*4)));

    }

    public void draw(SpriteBatch batch) {
        batch.draw(bg,bounds.x,bounds.y,bounds.width,bounds.height);

        for(CinematicPanelWidget panelWidget : panelWidgets) {
            panelWidget.draw(batch, scrollY);
        }

        if(cinematicPanel.widgetDrag) {
            cinematicPanel.selectedWidget.draw( batch, cursor.getPosition());
        }
    }

}
