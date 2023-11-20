package com.javaGame.projectile;

import com.javaGame.handler.Stocker;
import com.javaGame.monster.AMonster;
import com.javaGame.tower.ATower;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

public class RailProjectile extends AProjectile{

    public RailProjectile(double goblinPositionX, double goblinPositionY, double towerPositionX, double towerPositionY, int speed, int damage, AMonster target, ATower tower, double x, double y, int dotDamage, int dotDuration) {
        super(goblinPositionX, goblinPositionY, towerPositionX, towerPositionY, speed, damage, target, tower, x, y, dotDamage, dotDuration, 0, "/game/assets/world/Png.png", "none");
    }

/*    @Override
    public void shot() {
        Line laser = new Line(x + 40, y + 20, x + 40, y + 20);
        laser.setStroke(Color.RED);
        laser.setStrokeWidth(3); // Ajustez l'épaisseur selon vos besoins

        Stocker.getInstance().getLevelPane().getChildren().add(laser);

        double targetX = target.getTranslateX() + target.getFitWidth() / 2;
        double targetY = target.getTranslateY() + target.getFitHeight() / 2;

        // Animation pour étendre le laser
        Timeline extendLaser = new Timeline(
                new KeyFrame(Duration.seconds(0.1),
                        new KeyValue(laser.endXProperty(), targetX),
                        new KeyValue(laser.endYProperty(), targetY))
        );

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.1), laser);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.1), laser);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setDelay(Duration.seconds(0.1)); // Délai pour voir le laser avant qu'il ne commence disparaître

        fadeOut.setOnFinished(event -> {
            Stocker.getInstance().getLevelPane().getChildren().remove(laser); // Supprimer le laser
            this.target.IsHit(damage, dotDamage, dotDuration, dotTick, this.dotType); // Appliquer les dégâts
        });

        extendLaser.play();
        fadeIn.play();
        fadeOut.play();
    }*/

    @Override
    public void shot() {
        double startX = x + 40;
        double startY = y + 20;
        double endX = target.getTranslateX() + target.getFitWidth() / 2;
        double endY = target.getTranslateY() + target.getFitHeight() / 2;

        Line laser = new Line(startX, startY, startX, startY);
        laser.setStrokeWidth(2);
        laser.setStroke(Color.PURPLE);

        Glow glow = new Glow(0.8);
        laser.setEffect(glow);

        DoubleProperty endXProperty = new SimpleDoubleProperty(startX);
        DoubleProperty endYProperty = new SimpleDoubleProperty(startY);
        laser.endXProperty().bind(endXProperty);
        laser.endYProperty().bind(endYProperty);

        Timeline extendLaser = new Timeline(
                new KeyFrame(Duration.seconds(0.05), new KeyValue(endXProperty, endX), new KeyValue(endYProperty, endY))
        );

        DoubleProperty startXProperty = new SimpleDoubleProperty(startX);
        DoubleProperty startYProperty = new SimpleDoubleProperty(startY);
        laser.startXProperty().bind(startXProperty);
        laser.startYProperty().bind(startYProperty);

        Timeline eraseLaser = new Timeline(
                new KeyFrame(Duration.seconds(0.05), new KeyValue(startXProperty, endX), new KeyValue(startYProperty, endY))
        );

        Stocker.getInstance().getLevelPane().getChildren().add(laser);

        extendLaser.setOnFinished(event -> {
            eraseLaser.play();
        });

        eraseLaser.setOnFinished(event -> {
            Stocker.getInstance().getLevelPane().getChildren().remove(laser);
            this.target.IsHit(damage, dotDamage, dotDuration, dotTick, this.dotType);
        });


        extendLaser.play();
    }



}