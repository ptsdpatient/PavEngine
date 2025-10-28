package com.pavengine.app;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class PathFinder {
    public boolean findingPath = false;
//    Cell[][] grid;
    public Array<Cell> grid = new Array<>();
    public boolean hasStart = false;
    public boolean hasEnd = false;
    Array<Cell> calculatePath = new Array<>(), exploredPath = new Array<>();
    Vector3 currentCell = new Vector3(), startPosition = new Vector3(), endPosition = new Vector3();
    int iterations = 0;

    public PathFinder() {

    }

    public void addCell(Vector3 position) {
        grid.add(new Cell(position, CellType.PATH));
    }

    public void calculateCost(Cell cell, Vector3 endPosition) {
        cell.hCost = cell.coordinates.dst(endPosition);
        cell.gCost = cell.coordinates.dst(startPosition);
        cell.fCost = cell.gCost + cell.hCost;
    }

    public Array<Vector3> findPath() {
//        currentCell=startPosition;
//        this.endPosition=endPosition;
        iterations = 0;
        findingPath = true;
        while (iterations < 60) {
            for (Cell cell : grid) {

                if (Math.abs(cell.coordinates.x - currentCell.x) <= 1 &&
                    Math.abs(cell.coordinates.y - currentCell.y) <= 1 &&
                    Math.abs(cell.coordinates.z - currentCell.z) <= 1) {
                    if (!(cell.coordinates.x == currentCell.x &&
                        cell.coordinates.y == currentCell.y &&
                        cell.coordinates.z == currentCell.z)) {
                        if (cell.type == CellType.PATH) {
                            calculatePath.add(cell);
                            cell.debugColor = Color.CYAN;
                            cell.type = CellType.EXPLORED;
                        }
                    }
                }
            }

            for (Cell cell : calculatePath) {
                calculateCost(cell, endPosition);
            }

            if (calculatePath.size > 0) {
                exploredPath.addAll(calculatePath);
//                exploredPath.sort((a, b) -> Float.compare(a.fCost, b.fCost));

                exploredPath.sort((a, b) -> {
                    int fCompare = Float.compare(a.fCost, b.fCost);
                    if (fCompare != 0) {
                        return fCompare; // lower fCost first
                    }
                    return Float.compare(a.hCost, b.hCost); // if fCost same, lower hCost first
                });

//                if(!exploredPath.get(0).isExplored) {
                exploredPath.get(0).type = CellType.ROAD;
                currentCell.set(exploredPath.get(0).coordinates);
                exploredPath.get(0).isExplored = true;
//                }

                calculatePath = new Array<>();
            }


//            print(currentCell.dst(endPosition) + " distance");
            if (currentCell.dst(endPosition) <= 1) break;

            iterations++;
        }

        findingPath = false;
        return new Array<>();
    }

    public void reset() {
        calculatePath = new Array<>();
        exploredPath = new Array<>();
        for (Cell cell : grid) {
            cell.hCost = 0;
            cell.fCost = 0;
            cell.gCost = 0;
            cell.isExplored = false;
            cell.isStart = false;
            cell.isEnd = false;
            cell.type = CellType.PATH;
            hasStart = false;
            hasEnd = false;
            cell.debugColor = Color.LIGHT_GRAY;
        }
    }
}
