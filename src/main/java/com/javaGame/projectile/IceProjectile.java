package com.javaGame.projectile;

import com.javaGame.handler.GameLoop;
import com.javaGame.handler.Stocker;
import com.javaGame.monster.AMonster;
import com.javaGame.tower.ATower;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

public class IceProjectile extends AProjectile{
    public IceProjectile(double goblinPositionX, double goblinPositionY, double towerPositionX, double towerPositionY, int speed, int damage, AMonster target, ATower tower, double x, double y, int dotDamage, int dotDuration) {
        super(goblinPositionX, goblinPositionY, towerPositionX, towerPositionY, speed, damage, target, tower, x, y, dotDamage, dotDuration, 0, "/game/assets/world/ice.png", "none");
    }

    @Override
    public void shot() {
        target.setSlowInit(true);
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

        transition.setOnFinished(event -> {
            GameLoop.getInstance().adjustMonsterSpeed(target, target.getSpeed()*0.50);
            this.target.IsHit(damage, dotDamage, dotDuration, dotTick, this.dotType);
            projectileTransitions = null;
            Stocker.getInstance().getProjectiles().remove(this);
            Stocker.getInstance().getLevelPane().getChildren().remove(projectile);
        });
        projectileTransitions = transition;
        Stocker.getInstance().getProjectiles().add(this);
        transition.setInterpolator(Interpolator.LINEAR);
        // Démarrer l'animation
        transition.play();
    }
}
