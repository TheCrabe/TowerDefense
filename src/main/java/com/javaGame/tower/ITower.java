package com.javaGame.tower;

import com.javaGame.monster.AMonster;
import com.javaGame.monster.Goblin;

public interface ITower {
    int getLevel();
    int getDamage();
    int getRange();
    int getSellPrice();
    int getUpgradePrice();


    void upgrade();
    void attack(AMonster goblin);
}