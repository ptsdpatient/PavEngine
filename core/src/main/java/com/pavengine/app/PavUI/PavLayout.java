package com.pavengine.app.PavUI;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class PavLayout {

    public final List<PavWidget> widgets = new ArrayList<>();
    public final PavAnchor anchor;
    public final PavFlex direction;
    public final float gap;
    public final float spriteWidth, spriteHeight;
    public TextureRegion background;
    public Color srColor;
    public boolean overflowX = false, overflowY = false, isHovered = false;
    public float maxWidth = 0f, maxHeight = 0f, topY = 0f, leftX = 0f;
    public Rectangle box = new Rectangle();
    public float totalWidth = 0, totalHeight = 0, startX = 0, startY = 0;
    public float renderHeight = 0;
    public float renderWidth = 0;
    float margin = 0;

    public PavLayout(PavAnchor anchor, PavFlex direction,
                     float gap, float spriteWidth, float spriteHeight) {
        this.anchor = anchor;
        this.direction = direction;
        this.gap = gap;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
    }

    public PavLayout(PavAnchor anchor, PavFlex direction,
                     float gap, float spriteWidth, float spriteHeight, float margin, TextureRegion background) {
        this.anchor = anchor;
        this.direction = direction;
        this.gap = gap;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.background = background;
        this.margin = margin;
    }

    public PavLayout(PavAnchor anchor, PavFlex direction,
                     float gap, float spriteWidth, float spriteHeight, float margin) {
        this.anchor = anchor;
        this.direction = direction;
        this.gap = gap;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.margin = margin;
    }

    public PavLayout(PavAnchor anchor, PavFlex direction,
                     float gap, float spriteWidth, float spriteHeight, TextureRegion background, float maxWidth, float maxHeight) {
        this.anchor = anchor;
        this.direction = direction;
        this.gap = gap;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.background = background;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    public void addSprite(PavWidget widget) {
        widgets.add(widget);
        widget.setSize(spriteWidth, spriteHeight);
    }

    public void draw(SpriteBatch sb, float worldWidth, float worldHeight) {

        int count = widgets.size();
        if (count == 0) return;

        totalWidth = (direction == PavFlex.ROW)
            ? count * spriteWidth + (count - 1) * gap
            : spriteWidth;

        totalHeight = (direction == PavFlex.COLUMN)
            ? count * spriteHeight + (count - 1) * gap
            : spriteHeight;

        startX = 0;
        startY = 0;

        renderHeight = totalHeight + 2 * margin;
        renderWidth = totalWidth + 2 * margin;

        if (maxHeight > 0 && renderHeight > maxHeight) {
            overflowY = true;
            renderHeight = maxHeight;
        } else if (maxHeight == 0 && renderHeight > worldHeight) {
            overflowY = true;

            renderHeight = worldHeight;
        }

        if (maxWidth > 0 && renderWidth > maxWidth) {
            overflowX = true;
            renderWidth = maxWidth;
        } else if (maxWidth == 0 && renderWidth > worldWidth) {
            overflowX = true;
            renderWidth = worldWidth;
        }


        switch (anchor) {
            case TOP_LEFT:
                startX = 0;
                startY = worldHeight;
                break;
            case TOP_CENTER:
                startX = (worldWidth - renderWidth) / 2f;
                startY = worldHeight - margin;
                break;
            case TOP_RIGHT:
                startX = worldWidth - renderWidth;
                startY = worldHeight - margin;
                break;
            case CENTER_LEFT:
                startX = 0;
                startY = (worldHeight + renderHeight) / 2f;
                break;
            case CENTER:
                startX = (worldWidth - renderWidth) / 2f;
                startY = (worldHeight + renderHeight) / 2f;
                break;
            case CENTER_RIGHT:
                startX = worldWidth - renderWidth;
                startY = (worldHeight + renderHeight) / 2f;
                break;
            case BOTTOM_LEFT:
                startX = margin;
                startY = renderHeight;
                break;
            case BOTTOM_CENTER:
                startX = (worldWidth - renderWidth) / 2f;
                startY = renderHeight;
                break;
            case BOTTOM_RIGHT:
                startX = worldWidth - renderWidth - spriteWidth / 2f - margin;
                startY = renderHeight + margin;
                break;
        }

        box.set(startX, startY - renderHeight, renderWidth, renderHeight);

        if (background != null) {
            sb.draw(background,
                startX + margin,
                startY - renderHeight + margin,
                renderWidth - 2 * margin,
                renderHeight - 2 * margin);
        }

        for (int i = 0; i < count; i++) {
            PavWidget widget = widgets.get(i);
            float x = startX + leftX;
            float y = startY + topY;

            if (direction == PavFlex.ROW) {
                x += i * (spriteWidth + gap + 12f);
                y -= spriteHeight;
            } else {
                y -= i * (widget.box.height + gap + 6f) + widget.box.height;
                x += 0;
            }

            widget.setPosition(x, y);

            if (widget.getCenter().y > box.y + box.height || widget.getCenter().y < box.y) {
                continue;
            }

            widget.render(sb);

        }
    }
}
