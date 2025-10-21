package com.pavengine.app;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Cell {

    public Vector3 position, coordinates;
    public CellType type;
    public Color debugColor = new Color(Color.LIGHT_GRAY);
    public BoundingBox bounds;
    public Vector3 center = new Vector3();
    public boolean isStart = false, isEnd = false;
    public float cellSize = 3, offset = 4f, hCost = 0, fCost = 0, gCost = 0;
    boolean isExplored = false;

    public Cell(Vector3 position, CellType type) {
        this.coordinates = position;
        this.position = new Vector3(10 + position.x * cellSize, position.y * cellSize, position.z * cellSize - 10);
        this.type = type;
        this.bounds = new BoundingBox(
            new Vector3(this.position).add(offset),
            new Vector3(this.position).add(cellSize)
        );
        bounds.getCenter(center);
    }

}
