package com.javaGame.tower;

import com.javaGame.Cell.Cell;

public class TowerFactory {
    public static ATower createTower(String type, int x, int y, int tileSize, Cell cell) {
        switch (type) {
            case "FireTower":
                return new FireTower(x, y, tileSize, cell);
            case "ArrowTower":
                return new ArrowTower(x, y, tileSize, cell);
            case "BombTower":
                return new BombTower(x, y, tileSize, cell);
            case "IceTower":
                return new IceTower(x, y, tileSize, cell);
            case "LaserTower":
                return new LaserTower(x, y, tileSize, cell);
            case "RailTower":
                return new RailTower(x, y, tileSize, cell);
            default:
                throw new IllegalArgumentException("Type de tour non supporté: " + type);
        }
    }
}
