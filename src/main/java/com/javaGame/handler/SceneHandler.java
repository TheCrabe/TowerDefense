package com.javaGame.handler;

import com.javaGame.scene.StartMenu;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SceneHandler extends Parent {
    private static SceneHandler instance;
    private static Stage primaryStage;
    private static Scene actualScene;

    // Constructeur privé
    private SceneHandler() {}

    public static SceneHandler getInstance() {
        if (instance == null) {
            instance = new SceneHandler();
        }
        return instance;
    }

    // Create Stage and set GameMenu as actual scene
    public static void init(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("TOWER DEFENSE");

        StartMenu startMenuPane = new StartMenu();
        actualScene = new Scene(startMenuPane.getRoot(), LevelHandler.getWIDTH(), LevelHandler.getHEIGHT());

        primaryStage.setScene(actualScene);
        primaryStage.show();
    }

    // Change scene
    public static void setScene(Scene newScene) {
        actualScene = newScene;
        primaryStage.setScene(actualScene);
    }
}
