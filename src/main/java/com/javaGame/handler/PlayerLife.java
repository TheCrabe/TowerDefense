package com.javaGame.handler;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.geometry.Pos;

public class PlayerLife {
    private static final int MAX_HEALTH = 3;
    private int health;
    private ImageView[] healthIcons;

    public PlayerLife(Pane root) {
        health = MAX_HEALTH;
        healthIcons = new ImageView[MAX_HEALTH];

        for (int i = 0; i < MAX_HEALTH; i++) {
            ImageView healthIcon = new ImageView();
            StackPane img = new StackPane(healthIcon);
            setImage(healthIcon, "game/assets/world/live.png"); // Chemin de vos images de vie
            StackPane.setAlignment(healthIcon, Pos.TOP_LEFT);
            img.setLayoutX(10 + i * 60); // Position X ajustée pour chaque image
            img.setTranslateY(10);
            healthIcon.setFitWidth(40); // Largeur de l'image
            healthIcon.setFitHeight(40); // Hauteur de l'image
            healthIcon.setPreserveRatio(true);
            healthIcon.toFront();
            root.getChildren().add(img);
            healthIcons[i] = healthIcon;
        }
    }

    private void setImage(ImageView imageView, String path) {
        Image image = new Image(path);
        imageView.setImage(image);
        if (image.isError()) {
            System.out.println("Erreur de chargement de l'image: " + path);
        }
    }

    public void takeDamage(int damage) {
        health -= damage;
        updateHealthDisplay();
    }

    public void resetHealth() {
        health = MAX_HEALTH;
        updateHealthDisplay();
    }

    private void updateHealthDisplay() {
        for (int i = 0; i < MAX_HEALTH; i++) {
            healthIcons[i].setVisible(i < health);
        }
    }

    // Méthode pour éditer la santé ( des fins de démonstration/test)
    public void setHealth(int newHealth) {
        if (newHealth >= 0 && newHealth <= MAX_HEALTH) {
            health = newHealth;
            updateHealthDisplay();
        }
    }
    public int getHealth() {
        return health;
    }

}
