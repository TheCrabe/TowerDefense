package com.javaGame.monster;

public interface IMonster {
    int getHealth();
    int getSpeed();
    int getReward();
    int getDamage();
    int[][] getCoordinates();
    int getType();
    String getTexture();

    void setHealth(int health);
    void setCoordinates(int[][] coordinates);
    void setTexture(String texture);
}