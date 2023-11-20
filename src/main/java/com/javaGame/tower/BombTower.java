package com.javaGame.tower;

import com.javaGame.Cell.Cell;
import com.javaGame.monster.AMonster;
import com.javaGame.projectile.BombProjectile;

public class BombTower extends ATower{
    public BombTower(double x, double y, int TILE_SIZE, Cell cell) {
        super(1, 25, 150, 200, 80, 150, x, y, TILE_SIZE, 5, 0, 0, "/game/assets/world/TD2Bomb.png", cell);
    }
    @Override
    public void upgrade() {
        level++;
        damage += 10;
        range += 100;
        sellPrice += 10;
        upgradePrice += 50;
        fireRate -=1.5f;
        cost += 50;
    }
    @Override
    public void attack(AMonster goblin) {
        if(goblin.getProvisionalHealth() <= 0){
            return;
        }
        startCooldown();
        BombProjectile bomb = new BombProjectile(goblin.getPositionOnScreen().getX(), goblin.getPositionOnScreen().getY(), this.getPositionOnScreen().getX(), this.getPositionOnScreen().getY(), 10, damage, goblin, this, this.x, this.y, this.dotDamage, this.dotDuration);
        bomb.shot();
    }



}

