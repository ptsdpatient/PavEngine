package com.pavengine.app.PavScreen;


import static com.pavengine.app.Debug.Draw.debugRectangle;
import static com.pavengine.app.Methods.files;
import static com.pavengine.app.Methods.lockCursor;
import static com.pavengine.app.Methods.print;
import static com.pavengine.app.PavEngine.blastRadius;
import static com.pavengine.app.PavEngine.credits;
import static com.pavengine.app.PavEngine.cursor;
import static com.pavengine.app.PavEngine.gameFont;
import static com.pavengine.app.PavEngine.overlayViewport;
import static com.pavengine.app.PavEngine.playerDamage;
import static com.pavengine.app.PavEngine.playerDamageRate;
import static com.pavengine.app.PavEngine.sprayLimit;
import static com.pavengine.app.PavEngine.uiControl;
import static com.pavengine.app.PavInput.GameWorldInput.gameWorldInput;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.pavengine.app.PavEngine;
import com.pavengine.app.PavScript.TurretStat;
import com.pavengine.app.PavUI.ClickBehavior;
import com.pavengine.app.PavUI.PavAnchor;
import com.pavengine.app.PavUI.PavFlex;
import com.pavengine.app.PavUI.PavLayout;
import com.pavengine.app.PavUI.PavWidget;
import com.pavengine.app.PavUI.TextButton;
import com.pavengine.app.PavUI.UpgradeButton;

import java.util.Objects;

public class UpgradeScreen extends PavScreen {

    public Array<PavLayout> upgradeLayout = new Array<>();
    Vector2 touch = new Vector2();
    TurretStat turretDamage = new TurretStat(30f, 20f, 50);
    TurretStat turretFireRate = new TurretStat(0.3f, -0.05f, 80); // base 0.3s between shots, -0.02s per upgrade
    TurretStat turretsprayLimit = new TurretStat(5f, 2.5f, 90);  // base 5s, +0.5s per upgrade
    TurretStat turretBlastRadius = new TurretStat(5f, 0.4f, 60);  // base 5s, +0.5s per upgrade
    private TextureRegion image = new TextureRegion(new Texture(files("images/upgrade.png")));
    private TextButton creditShow;
    private float drawX, drawY, drawWidth, drawHeight;

    public UpgradeScreen(PavEngine game) {
        super(game);

        upgradeLayout.add(new PavLayout(PavAnchor.CENTER, PavFlex.COLUMN, 6, 720, 32, 5));
        upgradeLayout.peek().addSprite(new TextButton("Click to upgrade the turret", gameFont[2], ClickBehavior.Nothing));
        upgradeLayout.peek().addSprite(new UpgradeButton("damage", 50, gameFont[2], ClickBehavior.Nothing, uiControl[3], uiControl[4], uiControl[6]));
        upgradeLayout.peek().addSprite(new UpgradeButton("fire rate", 80, gameFont[2], ClickBehavior.Nothing, uiControl[3], uiControl[4], uiControl[6]));
        upgradeLayout.peek().addSprite(new UpgradeButton("spray limit", 90, gameFont[2], ClickBehavior.Nothing, uiControl[3], uiControl[4], uiControl[6]));
        upgradeLayout.peek().addSprite(new UpgradeButton("blast radius", 60, gameFont[2], ClickBehavior.Nothing, uiControl[3], uiControl[4], uiControl[6]));

        upgradeLayout.add(new PavLayout(PavAnchor.TOP_RIGHT, PavFlex.COLUMN, 0, 192, 16, 8));
        creditShow = new TextButton(String.valueOf(PavEngine.credits), gameFont[2], ClickBehavior.Nothing);
        upgradeLayout.peek().addSprite(creditShow);

        upgradeLayout.add(new PavLayout(PavAnchor.BOTTOM_RIGHT, PavFlex.COLUMN, 0, 164, 16, 8));
        upgradeLayout.peek().addSprite(new TextButton("NEXT LEVEL", gameFont[2], ClickBehavior.Nothing));

    }

    @Override
    public void debug() {
        for(PavLayout layout :  upgradeLayout) {
            debugRectangle(layout.box, Color.YELLOW);
            for(PavWidget widget : layout.widgets) {
                debugRectangle(widget.box, Color.BLUE);
            }
        }
    }

    public void applyTurretStats() {

        playerDamage = turretDamage.value;
        playerDamageRate = turretFireRate.value;
        sprayLimit = turretsprayLimit.value;
        blastRadius = turretBlastRadius.value;

    }

    @Override
    public void draw(float delta) {


        batch.draw(image, 0, 0, resolution.x, resolution.y);
        creditShow.text = ((int) credits) + " Credits";



        for (PavLayout layout : upgradeLayout) {
            layout.draw(batch, overlayViewport.getWorldWidth(), overlayViewport.getWorldHeight());
            for (PavWidget widget : layout.widgets) {
                if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                    if (cursor.clicked(widget.box)) {

                        if (Objects.equals(widget.text, "NEXT LEVEL")) {
                            print("yes");
                            applyTurretStats();
                            PavEngine.gamePause = false;
                            PavEngine.enableCursor = false;
                            lockCursor(true);
                            PavEngine.levelStatus = false;
                            game.setGameScreen();
                            break;
                        }

                        if (widget.upgradeIndex < 5) {

                            switch (widget.text) {
                                case "damage":
                                    if (credits >= turretDamage.cost) {
                                        turretDamage.upgrade(widget.upgradeIndex);
                                        widget.upgradeIndex++;
                                    }
                                    break;
                                case "fire rate":
                                    if (credits >= turretFireRate.cost) {
                                        turretFireRate.upgrade(widget.upgradeIndex);
                                        widget.upgradeIndex++;
                                    }
                                    break;
                                case "spray limit":
                                    if (credits >= turretsprayLimit.cost) {
                                        turretsprayLimit.upgrade(widget.upgradeIndex);
                                        widget.upgradeIndex++;
                                    }
                                    break;
                                case "blast radius":
                                    if (credits >= turretBlastRadius.cost) {
                                        turretBlastRadius.upgrade(widget.upgradeIndex);
                                        widget.upgradeIndex++;
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }


    @Override
    public void setInput() {
        Gdx.input.setInputProcessor(gameWorldInput);
    }


    @Override
    public void world(float delta) {

    }



}

