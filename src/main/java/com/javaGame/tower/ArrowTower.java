package com.javaGame.tower;

import com.javaGame.Cell.Cell;
import com.javaGame.monster.AMonster;
import com.javaGame.projectile.ArrowProjectile;

public class ArrowTower extends ATower{
    public ArrowTower(double x, double y, int TILE_SIZE, Cell cell) {
        super(1, 10, 250, 100, 80, 150, x, y, TILE_SIZE, 1, 0, 0, "/game/assets/world/TD2.png", cell);
    }


    @Override
    public void upgrade() {
    level++;
    damage += 10;
    range += 100;
    sellPrice += 10;
    upgradePrice += 50;
    fireRate -=0.25f;
    cost += 50;
    }

    @Override
    public void attack(AMonster goblin) {
        if(goblin.getProvisionalHealth() <= 0){
            return;
        }
        startCooldown();
        ArrowProjectile arrowProjectile = new ArrowProjectile(goblin.getPositionOnScreen().getX(), goblin.getPositionOnScreen().getY(), this.getPositionOnScreen().getX(), this.getPositionOnScreen().getY(), 10, damage, goblin, this, this.x, this.y, dotDamage, dotDuration);
        arrowProjectile.shot();
        goblin.initHit(this.getDamage(), false);
    }


}
