package com.javaGame;

import com.javaGame.handler.SceneHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        SceneHandler.init(primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }
}