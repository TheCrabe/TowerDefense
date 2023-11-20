package com.javaGame.tower;

import com.javaGame.Cell.Cell;
import com.javaGame.handler.GameLoop;
import com.javaGame.handler.Stocker;
import com.javaGame.monster.AMonster;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public abstract class ATower extends ImageView implements ITower {
    protected int cost;
    protected int level;
    protected int damage;
    protected int range;
    protected int sellPrice;
    protected int upgradePrice;
    protected double x;
    protected double y;
    protected float fireRate;
    protected boolean coolDown;
    protected Timeline cooldownTimeline;
    protected int dotDamage = 0;
    protected int dotDuration = 0;
    protected String texture;
    protected Cell cell;
    protected boolean deleted;


    public ATower(int level, int damage, int range, int cost, int sellPrice, int upgradePrice, double x, double y, int TILE_SIZE, float fireRate, int dotDamage, int dotDuration, String texture, Cell cell) {
        this.level = level;
        this.damage = damage;
        this.range = range;
        this.sellPrice = sellPrice;
        this.upgradePrice = upgradePrice;
        this.setTranslateX(x * TILE_SIZE - TILE_SIZE );
        this.setTranslateY(y * TILE_SIZE - TILE_SIZE);
        this.x = x * TILE_SIZE - TILE_SIZE;
        this.y = y * TILE_SIZE - TILE_SIZE;
        this.fireRate = fireRate;
        this.coolDown = false;
        this.texture = texture;
        this.dotDamage = dotDamage;
        this.dotDuration = dotDuration;
        this.cost = cost;
        this.cell = cell;
    }

    public int getLevel() {
        return level;
    }


    public int getDamage() {
        return damage;
    }

    public int getRange() {
        return range;
    }

    public int getSellPrice() {
        return sellPrice;
    }

    public int getUpgradePrice() {
        return upgradePrice;
    }

    public void upgrade() {
        level++;
        damage += 10;
        range += 100;
        sellPrice += 10;
        upgradePrice += 50;
        fireRate -=2;
        cost += 50;
    }


    public void attack(AMonster goblin){};

    public boolean CheckInRange(Point2D goblinScreenCoords) {
        Point2D towerScreenCoords = getPositionOnScreen();
        if (towerScreenCoords == null) {
            return false;
        }

        double distance = Math.sqrt(Math.pow(goblinScreenCoords.getX() - towerScreenCoords.getX(), 2) + Math.pow(goblinScreenCoords.getY() - towerScreenCoords.getY(), 2));

        return distance <= this.range;
    }
    public void attackMechant(AMonster goblin) {
        if(this instanceof IceTower && goblin.isSlowInit()){
            return;
        }
        if(this instanceof FireTower && goblin.hasDot()||this instanceof FireTower && goblin.dotInit()){
            return;
        }
        if (this.coolDown) {
            return;
        } else{
            if (CheckInRange(goblin.getPositionOnScreen())) {
                if(goblin.getProvisionalHealth() <= 0){
                    return;
                }
                attack(goblin);
            }
        }

    }
    public void startCooldown() {
        this.coolDown = true;
        // Crée un Timeline qui s'exécutera une seule fois
        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(this.fireRate), //
                ae -> this.coolDown = false
        ));
        this.cooldownTimeline = timeline;
        timeline.setCycleCount(1); // S'exécute une seule fois
        timeline.play(); //
    }

    public Timeline getCooldownTimeline() {
        return cooldownTimeline;
    }

    public Point2D getPositionOnScreen() {
        if (this.getScene() == null || this.getScene().getWindow() == null) {
            return null;
        }
        Point2D screenCoords = this.localToScreen(0, 0);
        return screenCoords;
    }

    public String getTexture() {
        return texture;
    }

    public int getCost() {  return cost; }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public int getSellCost() {
        return ((cost-50)/2);
    }

    public void sell() {
        Node parentContainer = this.getParent();
        GameLoop.getInstance().cleanPopUp();
        this.setVisible(false);
        this.setDisable(true);
        this.setOnMouseClicked(null);
        this.cell.setTower(null);
        this.setMouseTransparent(true);
        Stocker.getInstance().getEconomy().addMoney(this.getSellCost());
        this.deleted = true;
        Stocker.getInstance().getLevelPane().getChildren().remove(this);
        Stocker.getInstance().getTowers().remove(this);
        ((Pane) parentContainer).getChildren().remove(this);
    }



    public Cell getCell() {
        return cell;
    }

    public void setCell(Cell cell) {
        this.cell = cell;
    }
    public boolean isDeleted() {
        return deleted;
    }
}
