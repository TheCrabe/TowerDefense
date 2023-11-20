package com.javaGame.projectile;

import com.javaGame.handler.Stocker;
import com.javaGame.monster.AMonster;
import com.javaGame.tower.ATower;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.effect.Bloom;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

public class LaserProjectile extends AProjectile{

    public LaserProjectile(double goblinPositionX, double goblinPositionY, double towerPositionX, double towerPositionY, int speed, int damage, AMonster target, ATower tower, double x, double y, int dotDamage, int dotDuration) {
        super(goblinPositionX, goblinPositionY, towerPositionX, towerPositionY, speed, damage, target, tower, x, y, dotDamage, dotDuration, 0, "/game/assets/world/Png.png", "none");
    }

    @Override
    public void shot() {
        Line laser = new Line();
        laser.setStartX(x + 40); // Position de départ du laser (ajustée  la tour)
        laser.setStartY(y + 20);

        laser.setEndX(target.getTranslateX() + target.getFitHeight() / 2);
        laser.setEndY(target.getTranslateY() + target.getFitWidth() / 2);

        laser.setStroke(Color.DARKGREEN); // Couleur du laser
        laser.setStrokeWidth(2); // Épaisseur du laser

        // Ajout d'un effet de brillance
        Bloom bloom = new Bloom();
        bloom.setThreshold(0.1);
        laser.setEffect(bloom);

        // Ajouter le laser  la scène
        Stocker.getInstance().getLevelPane().getChildren().add(laser);

        // Appliquer les dégâts
        this.target.IsHit(damage, dotDamage, dotDuration, dotTick, this.dotType);

        // Animation pour rendre le laser plus dynamique
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.1), laser);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> Stocker.getInstance().getLevelPane().getChildren().remove(laser));
        fadeOut.play();
    }
}
