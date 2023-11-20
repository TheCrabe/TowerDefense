package com.javaGame.monster;

import com.javaGame.handler.GameScore;
import com.javaGame.handler.Stocker;
import javafx.animation.*;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public abstract class AMonster extends ImageView implements IMonster {
    protected int health;

    public int getProvisionalHealth() {
        return provisionalHealth;
    }

    protected int provisionalHealth;
    protected int speed;
    protected int reward;
    protected int damage;
    protected int[][] coordinates;
    protected int type;
    protected String texture;
    protected int size;
    protected int dotDamage;
    protected int dotDuration;
    protected Timeline dotTimeline;
    protected String dotType;
    protected boolean hasDot = false;
    protected boolean dotInit = false;

    protected Transition transition;
    protected boolean isSlow = false;
    protected boolean SlowInit = false;
    protected boolean isDead = false;

    public AMonster(int health, int speed, int reward, int damage, int[][] coordinates, int type, String texture, int size) {
        this.health = health;
        this.speed = speed;
        this.reward = reward;
        this.damage = damage;
        this.coordinates = coordinates;
        this.type = type;
        this.texture = texture;
        this.setTranslateX(coordinates[0][0]);
        this.setTranslateY(coordinates[0][1]);
        this.provisionalHealth = health;
        this.size = size;
    }

    public int getHealth() {
        return health;
    }

    public int getSpeed() {
        return speed;
    }

    public int getReward() {
        return reward;
    }

    public int getDamage() {
        return damage;
    }

    public int[][] getCoordinates() {
        return coordinates;
    }

    public int getType() {
        return type;
    }

    public String getTexture() {
        return texture;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public void setCoordinates(int[][] coordinates) {
        this.coordinates = coordinates;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }
    public Point2D getPositionOnScreen() {
        if (this.getScene() == null || this.getScene().getWindow() == null) {
            return null; // Le nœud n'est pas encore sur une scène ou une fenêtre
        }
        Point2D screenCoords = this.localToScreen(0, 0);
        return screenCoords;
    }
    public void IsHit(int damage, int dotDamage, int dotDuration, float dotTick, String dotType) {
        getDamage(damage, "none");
        if (this.health <= 0) {
            this.die();
        }
        if (dotDamage > 0 && !hasDot) {
            hasDot = true;
            this.dotDamage = dotDamage;
            this.dotDuration = dotDuration;
            this.dotType = dotType;
            applyDot(dotDamage, dotDuration, dotTick, dotType);
        }
    }
    public void getDamage(int damage, String dotType) {
        this.health -= damage;
        showDamageText(damage, dotType);
    }

    public void showDamageText(int damage, String dotType) {
        Text damageText = new Text(String.valueOf(damage));
        damageText.setX(getTranslateX()); // Position X du monstre
        damageText.setY(getTranslateY()); // Position Y du monstre
        if(dotType.equals("fire")) {
            damageText.setStyle("-fx-fill: red; -fx-font-size: 20px; -fx-font-weight: bold;");
        }

        Stocker.getInstance().getLevelPane().getChildren().add(damageText);


        TranslateTransition transition = new TranslateTransition(Duration.seconds(1), damageText);
        transition.setByY(-30); // Déplacer le texte de 30 pixels vers le haut
        transition.setOnFinished(event -> Stocker.getInstance().getLevelPane().getChildren().remove(damageText)); // Supprimer après l'animation
        transition.play();
    }

    public void initHit(int damage, boolean isFire) {

        if (this.provisionalHealth > 0) {
            this.provisionalHealth -= damage;
            if (this.provisionalHealth <= 0) {
                this.provisionalHealth = 0;
            }
        }
        if (isFire){
            dotInit = true;
        }
    }
    public void applyDot(int dotDamage, int dotDuration, double dotTick, String dotType) {
        int repeatCount = (int) (dotDuration / dotTick);
        hasDot = true; // Indique que le DOT a été appliqué

        dotTimeline = new Timeline(new KeyFrame(Duration.seconds(dotTick), e -> {
            getDamage(dotDamage, dotType);
            if (this.health <= 0) {
                this.die();
                dotTimeline.stop();
            }
        }));
        dotTimeline.setOnFinished(e -> {
            hasDot = false;
            dotInit = false;
        });
        dotTimeline.setCycleCount(repeatCount);
        dotTimeline.play();
    }

    public void pauseDot() {
        if (dotTimeline != null) {
            dotTimeline.pause();
        }
    }

    public void resumeDot() {
        if (dotTimeline != null && !dotTimeline.getStatus().equals(Animation.Status.RUNNING) && hasDot) {
            dotTimeline.play();
        }
    }



    public void die() {

        Stocker.getInstance().getEconomy().addMoney(this.reward);

        this.isDead = true;
        this.setVisible(false);
        this.setDisable(true);
        this.transition.stop();
        Stocker.getInstance().getGoblins().remove(this);
        Stocker.getInstance().getLevelPane().getChildren().remove(this);
        Stocker.getInstance().getScore().addScore(this.reward);


    }

    public int getSize() {
        return size;
    }

    public void setDotDamage(int dotDamage) {
        this.dotDamage = dotDamage;
    }

    public void setDotDuration(int dotDuration) {
        this.dotDuration = dotDuration;
    }
    public void setTransition(Transition transition) {
        this.transition = transition;
    }

    public PathTransition getTransition() {
        return (PathTransition) transition;
    }

    public boolean isSlow() {
        return isSlow;
    }

    public void setSlow(boolean b) {
        isSlow = b;
    }

    public boolean hasDot() {
        return hasDot;
    }
    public boolean dotInit() {
        return dotInit;
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean isSlowInit() {
        return SlowInit;
    }

    public void setSlowInit(boolean b) {
        SlowInit = b;
    }


}
