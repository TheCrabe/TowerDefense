package com.javaGame.tower;

import com.javaGame.Cell.Cell;
import com.javaGame.monster.AMonster;
import com.javaGame.projectile.IceProjectile;

public class IceTower extends ATower{
    public IceTower(double x, double y, int TILE_SIZE, Cell cell) {
        super(1, 15, 200, 50, 80, 150, x, y, TILE_SIZE, 1, 0, 0, "/game/assets/world/TD2Ice.png", cell);
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
        IceProjectile arrowProjectile = new IceProjectile(goblin.getPositionOnScreen().getX(), goblin.getPositionOnScreen().getY(), this.getPositionOnScreen().getX(), this.getPositionOnScreen().getY(), 10, damage, goblin, this, this.x, this.y, this.dotDamage, this.dotDuration);
        arrowProjectile.shot();
        goblin.initHit(this.damage, false);
    }
}
