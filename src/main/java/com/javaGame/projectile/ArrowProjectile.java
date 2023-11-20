package com.javaGame.projectile;

import com.javaGame.monster.AMonster;
import com.javaGame.tower.ATower;

public class ArrowProjectile extends AProjectile{
    public ArrowProjectile(double goblinPositionX, double goblinPositionY, double towerPositionX, double towerPositionY, int speed, int damage, AMonster target, ATower tower, double x, double y, int dotDamage, int dotDuration) {
        super(goblinPositionX, goblinPositionY, towerPositionX, towerPositionY, speed, damage, target, tower, x, y, dotDamage, dotDuration, 0, "/game/assets/world/Png.png", "none");
    }

}
