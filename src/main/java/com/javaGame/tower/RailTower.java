package com.javaGame.tower;

import com.javaGame.Cell.Cell;
import com.javaGame.monster.AMonster;
import com.javaGame.projectile.RailProjectile;

public class RailTower extends ATower{
    public RailTower(double x, double y, int TILE_SIZE, Cell cell) {
        super(1, 100, 500, 300, 80, 150, x, y, TILE_SIZE, 2, 0, 0, "/game/assets/world/TD2Rail.png", cell);
    }



    @Override
    public void attack(AMonster goblin) {
        if(goblin.getProvisionalHealth() <= 0){
            return;
        }
        startCooldown();
        RailProjectile arrowProjectile = new RailProjectile(goblin.getPositionOnScreen().getX(), goblin.getPositionOnScreen().getY(), this.getPositionOnScreen().getX(), this.getPositionOnScreen().getY(), 10, damage, goblin, this, this.x, this.y, dotDamage, dotDuration);
        arrowProjectile.shot();
        goblin.initHit(this.getDamage(), false);
    }
    @Override
    public void upgrade() {
        level++;
        damage += 125;
        range += 50;
        sellPrice += 10;
        upgradePrice += 50;
        fireRate -= 0.5f;
        cost += 50;
    }
}