package com.javaGame.handler;

import com.javaGame.Cell.Cell;
import com.javaGame.monster.AMonster;
import com.javaGame.projectile.AProjectile;
import com.javaGame.tower.ATower;
import javafx.animation.PathTransition;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polyline;

import java.util.ArrayList;
import java.util.List;

public class Stocker {
    private static Stocker instance;

    private static Polyline path;
    private static Pane levelPane;
    private static Cell[][] coordinates;
    private static List<AMonster> goblins = new ArrayList<>();
    private static List<ATower> towers = new ArrayList<>();
    private static List<String> waveMonsters = new ArrayList<>();
    private static List<AProjectile> projectiles = new ArrayList<>();
    private static int level = 1;
    private int waveNumber = 0;
    private PlayerLife playerLife;
    private GameEconomy economy;
    private GameScore score;
    private boolean isChangingLevel = true;
    private List<PathTransition> goblinTransitions = new ArrayList<>();

    private List<Timeline> AllCooldown = new ArrayList<>();

    // Constructeur privé
    private Stocker() {}

    public static Stocker getInstance() {
        if (instance == null) {
            instance = new Stocker();
        }
        return instance;
    }

    public  List<AProjectile> getProjectiles() {
        return projectiles;
    }

    public boolean isChangingLevel() {
        return isChangingLevel;
    }

    public void setChangingLevel(boolean isChangingLevel) {
        this.isChangingLevel = isChangingLevel;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int newLevel) {
        level = newLevel;
    }

    public int getWaveNumber() {
        return waveNumber;
    }

    public void setWaveNumber(int waveNumber) {
        this.waveNumber = waveNumber;
    }

    public  List<String> getWaveMonsters() {
        return waveMonsters;
    }

    public  void setWaveMonsters(List<String> waveMonsters) {
        Stocker.waveMonsters = waveMonsters;
    }

    public List<AMonster> getGoblins() {
        return goblins;
    }

    public List<ATower> getTowers() {
        return towers;
    }

    public void setTowers(List<ATower> newTowers) {
        towers = newTowers;
    }

    public Cell[][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Cell[][] newCoordinates) {
        coordinates = newCoordinates;
    }

    public Pane getLevelPane() {
        return levelPane;
    }

    public void setLevelPane(Pane newLevelPane) {
        levelPane = newLevelPane;
    }

    public Polyline getPath() {
        return path;
    }

    public void setPath(Polyline newPath) {
        path = newPath;
    }

    public PlayerLife getPlayerLife() {
        return playerLife;
    }

    public void setPlayerLife(PlayerLife newPlayerLife) {
        this.playerLife = newPlayerLife;
    }

    public void reset() {
        for (AMonster monster : goblins) {
            monster.die();
        }
        path = null;
        levelPane = null;
        coordinates = null;
        towers.clear();
        waveMonsters.clear();
        waveNumber = 0;
    }


    public void addMonster(AMonster monster) {
        goblins.add(monster);
    }

    public List<PathTransition> getMonsterTransitions() {
        return goblinTransitions;
    }

    public void setMonsterTransitions(List<PathTransition> goblinTransitions) {
        this.goblinTransitions = goblinTransitions;
    }

    public void setEconomy(GameEconomy economy) {
        this.economy = economy;
    }

    public GameEconomy getEconomy() {
        return economy;
    }
    public GameScore getScore(){
        return score;
    }
    public void setScore(GameScore gameScore){
        this.score = gameScore;
    }
    public List<Timeline> getAllCooldown() {
        return AllCooldown;
    }
}
