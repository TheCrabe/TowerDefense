package com.javaGame.projectile;

import com.javaGame.handler.Stocker;
import com.javaGame.monster.AMonster;
import com.javaGame.tower.ATower;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public abstract class AProjectile {
    protected TranslateTransition projectileTransitions;
    AMonster target;
    ATower tower;
    double goblinPositionX;
    double goblinPositionY;
    double towerPositionX;
    double towerPositionY;
    int speed;
    int damage;
    double x;
    double y;
    int dotDamage;
    int dotDuration;
    float dotTick = 0;
    String dotType;
    String texture;


    public AProjectile(double goblinPositionX, double goblinPositionY, double towerPositionX, double towerPositionY, int speed, int damage, AMonster target, ATower tower, double x, double y, int dotDamage, int dotDuration, float dotTick, String texture, String dotType) {
        this.goblinPositionX = goblinPositionX;
        this.goblinPositionY = goblinPositionY;
        this.towerPositionX = towerPositionX;
        this.towerPositionY = towerPositionY;
        this.speed = speed;
        this.damage = damage;
        this.target = target;
        this.tower = tower;
        this.x = x;
        this.y = y;
        this.dotDamage = dotDamage;
        this.dotDuration = dotDuration;
        this.dotTick = dotTick;
        this.texture = texture;
        this.dotType = dotType;
    }

    public void shot() {
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
            projectileTransitions = null;
            Stocker.getInstance().getProjectiles().remove(this);

            this.target.IsHit(damage, dotDamage, dotDuration, dotTick, dotType);
            Stocker.getInstance().getLevelPane().getChildren().remove(projectile);
        });
        projectileTransitions = transition;
        Stocker.getInstance().getProjectiles().add(this);
        transition.setInterpolator(Interpolator.LINEAR);
        transition.play();
    }
    public void pauseProjectiles() {

        projectileTransitions.pause();

    }
    public void resumeProjectiles() {

        projectileTransitions.play();

    }


}