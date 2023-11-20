package com.javaGame.handler;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Pos;

public class GameScore {

    private int score;
    private Text ScoreDisplay;


    public GameScore(Pane root) {

        ScoreDisplay = new Text("Score: " +score);
        ScoreDisplay.setFont(new Font(20)); // Taille de la police
        StackPane.setAlignment(ScoreDisplay, Pos.TOP_LEFT);
        ScoreDisplay.setLayoutX(25); // Position X
        ScoreDisplay.setLayoutY(100); // Position Y ajustée pour être en dessous de la vie
        ScoreDisplay.setFont(new Font("Arial", 24));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(3.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.4, 0.4));
        ScoreDisplay.setStroke(Color.BLACK);
        ScoreDisplay.setStrokeWidth(2);
        ScoreDisplay.setEffect(dropShadow);
        root.getChildren().add(ScoreDisplay);
    }
    public void addScore(int points) {
        score += points * Stocker.getInstance().getPlayerLife().getHealth();
        updateScoreDisplay();
    }


    private void updateScoreDisplay() {
        ScoreDisplay.setText("Score: " +score);
    }

    public int getScore() {
        return score;    }
}
