package com.javaGame.tower;

import com.javaGame.Cell.Cell;
import com.javaGame.monster.AMonster;
import com.javaGame.projectile.LaserProjectile;

public class LaserTower extends ATower{
    public LaserTower(double x, double y, int TILE_SIZE, Cell cell) {
        super(1, 1, 150, 250, 80, 150, x, y, TILE_SIZE, 0.02f, 0, 0, "/game/assets/world/TD2Laser.png", cell);
    }



    @Override
    public void attack(AMonster goblin) {
        if(goblin.getProvisionalHealth() <= 0){
            return;
        }
        startCooldown();
        LaserProjectile arrowProjectile = new LaserProjectile(goblin.getPositionOnScreen().getX(), goblin.getPositionOnScreen().getY(), this.getPositionOnScreen().getX(), this.getPositionOnScreen().getY(), 10, damage, goblin, this, this.x, this.y, dotDamage, dotDuration);
        arrowProjectile.shot();
        goblin.initHit(this.getDamage(), false);
    }
    @Override
    public void upgrade() {
        level++;
        damage += 1;
        range += 100;
        sellPrice += 10;
        upgradePrice += 50;
        cost += 50;
    }
}
