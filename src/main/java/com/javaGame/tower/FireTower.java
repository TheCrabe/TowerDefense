package com.javaGame.tower;

import com.javaGame.Cell.Cell;
import com.javaGame.monster.AMonster;
import com.javaGame.projectile.FireProjectile;

public class FireTower extends ATower{
    public FireTower(double x, double y, int TILE_SIZE, Cell cell) {
        super(1, 5, 200, 150, 80, 150, x, y, TILE_SIZE, 0.75f, 5, 5, "/game/assets/world/TD2Fire.png", cell);
    }
    @Override
    public void upgrade() {
        level++;
        damage += 10;
        range += 100;
        sellPrice += 10;
        upgradePrice += 50;
        fireRate -=0.125f;
        cost += 50;
    }
    @Override
    public void attack(AMonster goblin) {
        if(goblin.getProvisionalHealth() <= 0){
            return;
        }
        startCooldown();
        FireProjectile arrowProjectile = new FireProjectile(goblin.getPositionOnScreen().getX(), goblin.getPositionOnScreen().getY(), this.getPositionOnScreen().getX(), this.getPositionOnScreen().getY(), 10, damage, goblin, this, this.x, this.y, this.dotDamage, this.dotDuration);
        arrowProjectile.shot();
        goblin.initHit(this.damage, true);
    }



}
