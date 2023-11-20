package com.javaGame.projectile;

import com.javaGame.handler.Stocker;
import com.javaGame.monster.AMonster;
import com.javaGame.tower.ATower;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class BombProjectile extends AProjectile{
    public BombProjectile(double goblinPositionX, double goblinPositionY, double towerPositionX, double towerPositionY, int speed, int damage, AMonster target, ATower tower, double x, double y, int dotDamage, int dotDuration) {
        super(goblinPositionX, goblinPositionY, towerPositionX, towerPositionY, speed, damage, target, tower, x, y, dotDamage, dotDuration, 0, "/game/assets/world/Bomb.png", "none");
    }
    @Override
    public void shot() {
        final List<AMonster> goblins = new ArrayList<>();
        for (AMonster goblin : Stocker.getInstance().getGoblins()) {
            if (CheckAoeRange(goblin.getPositionOnScreen())) {
                goblins.add(goblin);
                goblin.initHit(damage, false);
            }
        }

        ImageView projectile = new ImageView(new Image(texture));
        projectile.setFitHeight(10); // Ajustez la taille selon vos besoins
        projectile.setFitWidth(10);
        projectile.setX(x + 40); // Ajustez la position selon vos besoins
        projectile.setY(y + 20);

        Stocker.getInstance().getLevelPane().getChildren().add(projectile);
        TranslateTransition transition = new TranslateTransition();
        transition.setNode(projectile);
        transition.setDuration(Duration.seconds(0.5)); // Durée de l'animation, a ajuster
        double targetCenterX = target.getPositionOnScreen().getX()+10;
        double targetCenterY = target.getPositionOnScreen().getY()+10;
        if(target.getSize() > 50){
            targetCenterX = target.getPositionOnScreen().getX() + target.getFitHeight() / 2;
            targetCenterY = target.getPositionOnScreen().getY() + target.getFitWidth() / 2;
        }
        transition.setToX(targetCenterX - towerPositionX );
        transition.setToY(targetCenterY - towerPositionY);
        Stocker.getInstance().getProjectiles().add(this);

        transition.setOnFinished(event -> {
            Stocker.getInstance().getLevelPane().getChildren().remove(projectile);
            for (AMonster goblin : goblins) {
                goblin.IsHit(damage, dotDamage, dotDuration, dotTick, this.dotType);
                Stocker.getInstance().getLevelPane().getChildren().remove(projectile);
            }
            projectileTransitions = null;
            Stocker.getInstance().getProjectiles().remove(this);

        });
        projectileTransitions = transition;
        transition.setInterpolator(Interpolator.LINEAR);
        // Démarrer l'animation
        transition.play();
    }
    public boolean CheckAoeRange(Point2D goblinScreenCoords) {
        Point2D goblinPos = target.getPositionOnScreen();
        if (goblinPos == null) {
            return false;
        }

        double distance = Math.sqrt(Math.pow(goblinScreenCoords.getX() - goblinPos.getX(), 2) + Math.pow(goblinScreenCoords.getY() - goblinPos.getY(), 2));

        return distance <= 50;
    }

}
