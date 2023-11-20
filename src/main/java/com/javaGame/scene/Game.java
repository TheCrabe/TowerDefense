package com.javaGame.scene;

import com.javaGame.handler.*;
import com.javaGame.monster.AMonster;
import com.javaGame.projectile.AProjectile;
import com.javaGame.tower.ATower;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.Arrays;

public class Game {
    private static GameEconomy economy;
    private static Label moneyLabel;
    private static Label lifeLabel;

    private static Game instance = null;
    private static boolean isPaused = false;
    private static ImageView selectedTower;
    private static HBox pauseMenu;
    private static int TypeTower = 0;

    public static Scene newGame(int level) {

        final Pane root = LevelHandler.getInstance().createField(level);
        PlayerLife playerLife = new PlayerLife(root);
        GameEconomy economy = new GameEconomy(root , 1000);
        GameScore gameScore = new GameScore(root);


        Stocker.getInstance().setPlayerLife(playerLife);
        Stocker.getInstance().setEconomy(economy);
        Stocker.getInstance().setScore(gameScore);


        HBox towerSelection = new HBox(10); // Espacement de 10 entre les boutons
        towerSelection.setAlignment(Pos.BOTTOM_RIGHT); // Position en bas a droite
        towerSelection.setPadding(new Insets(10)); // Marge intérieure
        initPauseMenu();

        root.getChildren().add(towerSelection);
        System.out.println("Menu de sélection des tours ajoute  la scène.");

        System.out.println("Taille du HBox: " + towerSelection.getBoundsInParent());

        String[] towerTypes = {"ArrowTower", "FireTower", "BombTower", "IceTower", "LaserTower", "RailTower"};
        String[] imagePaths = {"/game/assets/world/TD2.png", "/game/assets/world/TD2Fire.png", "/game/assets/world/TD2Bomb.png", "/game/assets/world/TD2Ice.png", "/game/assets/world/TD2Laser.png", "/game/assets/world/TD2Rail.png"};
        Button[] buttons = new Button[towerTypes.length];
        String[] towerDescriptions = {
                "ArrowTower\n-Tir moyen\n-Cout : 100",
                "FireTower\n-Tir rapide \n Enflamme les ennemis\n-Cout : 150",
                "BombTower\n-Tir lent\n-Explosion de zone\n-Cout : 200",
                "IceTower\n-Tir rapide\n-Ralentit les ennemis\n-Cout : 50",
                "LaserTower\n-Tir tres rapide\n-fait peu de degat\n-Cout : 250",
                "RailTower\n-Tir lent\n-Longue porte et coup puissant\n-Cout : 300"
        };
        for (int i = 0; i < towerTypes.length; i++) {
            Image image = new Image(imagePaths[i]);
            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            imageView.setPreserveRatio(true);
            imageView.toFront();

            // Initialisez le bouton avant d'installer le Tooltip
            buttons[i] = new Button("", imageView);
            buttons[i].setFocusTraversable(false);
            buttons[i].setStyle("-fx-background-color: transparent;");

            // Maintenant, créez et installez le Tooltip
            Tooltip tooltip = new Tooltip(towerDescriptions[i]);
            Tooltip.install(buttons[i], tooltip);
            tooltip.setStyle("-fx-font-size: 10px; -fx-background-color: white; -fx-text-fill: black;");
            tooltip.setShowDelay(Duration.millis(100)); // Affiche le tooltip après un court délai, par exemple 100 millisecondes


            final String towerType = towerTypes[i];
            int finalI = i;
            buttons[i].setOnAction(event -> {
                GameLoop.getInstance().setTower(towerType);
                setImage(imagePaths[finalI]);
            });

            towerSelection.getChildren().add(buttons[i]);
        }


        towerSelection.setLayoutY(10);
        towerSelection.setLayoutX(200);

        Scene scene = new Scene(root, LevelHandler.getWIDTH(), LevelHandler.getHEIGHT());
        initPauseMenu();
        GameLoop.getInstance().start();


        selectedTower = new ImageView();
        StackPane img = new StackPane(selectedTower); // Ajoutez selectedTower  img
        setImage("game/assets/world/TD2.png");
        StackPane.setAlignment(selectedTower, Pos.TOP_RIGHT);
        img.setLayoutX(root.getWidth() - selectedTower.getFitWidth());
        img.setTranslateX(-60);
        img.setTranslateY(10);
        selectedTower.toFront();
        selectedTower.setFitWidth(50); // Largeur de l'image
        selectedTower.setFitHeight(50); // Hauteur de l'image
        selectedTower.setPreserveRatio(true); //
        root.getChildren().add(img);

        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DIGIT1 -> {
                        GameLoop.getInstance().setTower("ArrowTower");
                        setImage("game/assets/world/TD2.png");
                        System.out.println("ArrowTower");

                }
                case DIGIT2 -> {

                        GameLoop.getInstance().setTower("FireTower");
                        setImage("game/assets/world/TD2Fire.png");
                        System.out.println("FireTower");

                }
                case DIGIT3 -> {

                    GameLoop.getInstance().setTower("BombTower");
                    setImage("game/assets/world/TD2Bomb.png");
                    System.out.println("BombTower");

                }
                case DIGIT4 -> {

                    GameLoop.getInstance().setTower("IceTower");
                    setImage("game/assets/world/TD2Ice.png");
                    System.out.println("IceTower");

                }
                case DIGIT5 -> {

                    GameLoop.getInstance().setTower("LaserTower");
                    setImage("game/assets/world/TD2Laser.png");
                    System.out.println("LaserTower");

                }
                case DIGIT6 -> {

                    GameLoop.getInstance().setTower("RailTower");
                    setImage("game/assets/world/TD2Rail.png");
                    System.out.println("RailTower");

                }
                case SPACE -> {
                    if (!isPaused) {
                        System.out.println("pause");
                        GameLoop.getInstance().stop();
                        showPauseMenu(root);

                        GameLoop.getInstance().pauseSpawning();
                        GameLoop.getInstance().pauseGoblinTransitions();
                        for (AProjectile projectile : Stocker.getInstance().getProjectiles()) {
                            projectile.pauseProjectiles();
                        }
                        isPaused = true;

                        for (AMonster monster : Stocker.getInstance().getGoblins()) {
                            monster.pauseDot();
                        }
                        for (ATower tower : Stocker.getInstance().getTowers()) {
                            tower.getCooldownTimeline().pause();
                        }
                    } else {
                        System.out.println("unpause");
                        hidePauseMenu();
                        GameLoop.getInstance().start();

                        GameLoop.getInstance().resumeSpawning();
                        for (AProjectile projectile : Stocker.getInstance().getProjectiles()) {
                            projectile.resumeProjectiles();
                        }
                        for (ATower tower : Stocker.getInstance().getTowers()) {
                            tower.getCooldownTimeline().play();
                        }

                        GameLoop.getInstance().resumeGoblinTransitions();for (AMonster monster : Stocker.getInstance().getGoblins()) {
                            monster.resumeDot();
                        }

                        isPaused = false;

                    }
                }
                case R -> {
                    SceneHandler.setScene(new Scene(new StartMenu().getRoot()));
                    GameLoop.getInstance().stop();
                    GameLoop.getInstance().reset();
                }
            }
        });
        return scene;
    }

    private static void initPauseMenu() {
        pauseMenu = new HBox(10); // Utilisez HBox avec un espacement de 10
        pauseMenu.setAlignment(Pos.CENTER); // Centrer les éléments horizontalement
        Button resumeButton = new Button("Resume");
        Button tutorielButton = new Button("Tutoriel");
        Button quitButton = new Button("Quit");
        pauseMenu.getChildren().addAll(resumeButton, tutorielButton, quitButton);
        resumeButton.setFocusTraversable(false);
        tutorielButton.setFocusTraversable(false);
        quitButton.setFocusTraversable(false);
        StackPane.setAlignment(pauseMenu, Pos.CENTER); // Si vous utilisez StackPane, centrez la HBox

        pauseMenu.setVisible(false); // Le menu est initialement invisible

        resumeButton.setOnAction(event -> {
            hidePauseMenu();
            GameLoop.getInstance().start(); // Reprend la boucle de jeu
            GameLoop.getInstance().resumeGoblinTransitions();// reprend le chemin
            isPaused = false;
        });

        tutorielButton.setOnAction(event -> {

            Stage tutorialStage = new Stage();

            Image image = new Image("/menu/images/Tutorial.png");
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(false);
            imageView.setFitHeight(LevelHandler.getHEIGHT());
            imageView.setFitWidth(LevelHandler.getWIDTH());


            Button closeButton = new Button("Fermer");
            closeButton.setOnAction(e -> tutorialStage.close());

            StackPane layout = new StackPane();
            layout.getChildren().add(imageView);
            layout.getChildren().add(closeButton);
            StackPane.setAlignment(closeButton, Pos.BOTTOM_CENTER);
            StackPane.setMargin(closeButton, new Insets(10));

            Scene tutorialScene = new Scene(layout);
            tutorialStage.setScene(tutorialScene);

            tutorialStage.showAndWait();
        });


        quitButton.setOnAction(event -> {
            SceneHandler.setScene(new Scene(new StartMenu().getRoot()));
            GameLoop.getInstance().stop();
            GameLoop.getInstance().reset();
            isPaused = false;

        });
    }
    private static void showPauseMenu(Pane gameRoot) {
        Platform.runLater(() -> {
            // S'il n'est pas déj dans la scène, ajoutez-le
            if (!gameRoot.getChildren().contains(pauseMenu)) {
                gameRoot.getChildren().add(pauseMenu);
            }
            pauseMenu.setVisible(true);
        });
    }

    private static void hidePauseMenu() {
        Platform.runLater(() -> {
            pauseMenu.setVisible(false);
            // Retirez le menu de pause de la scène s'il a été ajouté précédemment
            if (pauseMenu.getParent() != null) {
                ((Pane)pauseMenu.getParent()).getChildren().remove(pauseMenu);
            }
        });
    }
    private static void setImage(String imagePath) {
        Image image = new Image(imagePath);
        selectedTower.setImage(image);
        image.errorProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                System.out.println("Erreur lors du chargement de l'image: " + imagePath);
            }
        });
        selectedTower.setImage(image);
    }

    public static void setIsPaused(boolean isPaused) {
        Game.isPaused = isPaused;
    }
}
