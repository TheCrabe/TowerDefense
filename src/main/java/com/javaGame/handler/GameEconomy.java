package com.javaGame.handler;

import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.geometry.Pos;

public class GameEconomy {
    protected int money;
    protected Text moneyDisplay;

    public GameEconomy(Pane root, int initialMoney) {
        money = initialMoney;

        moneyDisplay = new Text("Money: " + money);
        moneyDisplay.setFont(new Font(20)); // Taille de la police
        StackPane.setAlignment(moneyDisplay, Pos.TOP_LEFT);
        moneyDisplay.setLayoutX(25); // Position X
        moneyDisplay.setLayoutY(75); // Position Y ajustée pour être en dessous de la vie
        moneyDisplay.setFont(new Font("Arial", 24));
        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(3.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.4, 0.4));
        moneyDisplay.setStroke(Color.BLACK);
        moneyDisplay.setStrokeWidth(2);
        moneyDisplay.setEffect(dropShadow);
        root.getChildren().add(moneyDisplay);
    }

    public void addMoney(int amount) {
        money += amount;
        updateMoneyDisplay();
    }

    public void spendMoney(int amount) {
        if (money >= amount) {
            money -= amount;
            updateMoneyDisplay();
        }
    }


    private void updateMoneyDisplay() {
        moneyDisplay.setText("Money: " + money);
    }

    // Méthodes pour obtenir et définir l'argent
    public int getMoney() {
        return money;
    }

    public void setMoney(int newMoney) {
        if (newMoney >= 0) {
            money = newMoney;
            updateMoneyDisplay();
        }
    }
}
