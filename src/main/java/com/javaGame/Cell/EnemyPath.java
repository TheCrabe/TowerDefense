package com.javaGame.Cell;

import javafx.scene.paint.Color;

public class EnemyPath extends Cell{
    protected String pattern;

    public EnemyPath(int[][] coordinates) {
        super(1, coordinates, "src/main/resources/game/assets/world/sand.png");
    }
    public EnemyPath(int[][] coordinates, String texture) {
        super(1, coordinates, "src/main/resources/game/assets/world/path"+texture+".png");
    }

    public String getPattern() { return pattern; }
    public void setPattern(String pattern) { this.pattern = pattern; }
}
