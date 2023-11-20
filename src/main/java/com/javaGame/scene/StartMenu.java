package com.javaGame.scene;

import com.javaGame.handler.LevelHandler;
import com.javaGame.handler.SceneHandler;
import com.javaGame.handler.Stocker;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

// Main class for the game menu, extending Application which is a JavaFX class for applications.
public class StartMenu extends StackPane {
    protected StackPane root;
    public StartMenu() {
        // Creating a new layout pane for startMenu
        this.root = new StackPane();

        // Creating an ImageView for background
        ImageView backgroundImage = new ImageView("/menu/images/background.png");
        backgroundImage.setFitWidth(LevelHandler.getWIDTH());
        backgroundImage.setFitHeight(LevelHandler.getHEIGHT());

        // Creating VerticalBox to store the startButton and the exitButton
        VBox startMenu = new VBox(10);
        startMenu.setAlignment(Pos.CENTER_LEFT);
        startMenu.setTranslateX(100);

        // Startbutton
        StackPane startButton = createButton("START");
        startButton.setOnMouseClicked(event -> {
            SceneHandler.setScene(Game.newGame(1));
        });
        StackPane LevelSelector = createButton("SELECTOR");
        LevelSelector.setOnMouseClicked(event -> {
            LevelSelector levelSelector = new LevelSelector();
            levelSelector.newSelector();
            Scene scene = new Scene(levelSelector.getRoot(), 800, 450);
            SceneHandler.setScene(scene);
        });
        // Exitbutton
        StackPane exitButton = createButton("EXIT");
        exitButton.setOnMouseClicked(event -> {
            // Terminate the application when exit is clicked
            System.exit(0);
        });

        startMenu.getChildren().addAll(startButton, LevelSelector, exitButton);

        this.root.getChildren().addAll(backgroundImage, startMenu);

    }

    public StackPane getRoot() {
        return root;
    }

    public StackPane createButton(String title) {
        StackPane button = new StackPane();

        // Set the button text and style
        Text text = new Text(title);
        text.setFont(Font.font(20));
        text.setFill(Color.WHITE);

        // Create a background rectangle for the button
        Rectangle bg = new Rectangle(250, 30);
        bg.setOpacity(0.6);
        bg.setFill(Color.BLACK);
        bg.setEffect(new GaussianBlur(3.5));

        button.setAlignment(Pos.CENTER_LEFT);
        // Add the background and text to the StackPane
        button.getChildren().addAll(bg, text);

        // Mouse event handlers to change the appearance of the button on hover
        button.setOnMouseEntered(event -> {
            bg.setTranslateX(10);
            text.setTranslateX(10);
            bg.setFill(Color.WHITE);
            text.setFill(Color.BLACK);
        });

        button.setOnMouseExited(event -> {
            bg.setTranslateX(0);
            text.setTranslateX(0);
            bg.setFill(Color.BLACK);
            text.setFill(Color.WHITE);
        });

        // Create a drop shadow effect to be applied when the button is pressed
        DropShadow drop = new DropShadow(50, Color.WHITE);
        drop.setInput(new Glow());

        // Mouse event handlers to apply or remove the effect on mouse press
        button.setOnMousePressed(event -> setEffect(drop));
        button.setOnMouseReleased(event -> setEffect(null));

        return button;
    }
}