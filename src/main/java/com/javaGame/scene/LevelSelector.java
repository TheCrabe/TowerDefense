package com.javaGame.scene;

import com.javaGame.handler.SceneHandler;
import com.javaGame.handler.Stocker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class LevelSelector extends StackPane {
    private static final int MAX_ROWS = 3;
    private static final int MAX_COLS = 3;
    protected StackPane root;

    public LevelSelector() {
        this.root = new StackPane();
    }
    public void newSelector() {
        final StackPane root = new StackPane();

        // Fond d'écran du sélecteur
        root.setBackground(new Background(new BackgroundFill(Color.LIGHTGRAY, CornerRadii.EMPTY, Insets.EMPTY)));

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); // Espacement horizontal entre les images
        grid.setVgap(10); // Espacement vertical entre les images
        grid.setPadding(new Insets(20, 20, 20, 20)); // Marge autour de la grille

        for (int i = 0; i < MAX_ROWS; i++) {
            for (int j = 0; j < MAX_COLS; j++) {
                int levelNumber = i * MAX_COLS + j + 1;

                String path = "game/assets/world/" + levelNumber + ".png";
                if (levelNumber > 3) {
                    path = "game/assets/world/wip.png";
                }
                ImageView levelImage = createLevelImage(path, levelNumber);

                // Redimensionnement des images pour qu'elles s'adaptent bien dans la grille
                levelImage.setFitHeight(250); // Hauteur fixe pour chaque image
                levelImage.setFitWidth(250);  // Largeur fixe pour chaque image
                levelImage.setPreserveRatio(true); // Préserver le ratio de l'image

                grid.add(levelImage, j, i);
            }
        }

        root.getChildren().add(grid);
        this.root.getChildren().addAll(root);
    }
    public StackPane getRoot() {
        return root;
    }
    private ImageView createLevelImage(String imagePath, int levelId) {
        Image image = new Image(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setUserData(levelId); // Storing level ID in the imageView

        imageView.addEventHandler(MouseEvent.MOUSE_CLICKED, event -> {
            ImageView source = (ImageView) event.getSource();
            int selectedLevel = (int) source.getUserData();
            onLevelSelected(selectedLevel);
        });

        // Optional: set size, style, etc. for imageView

        return imageView;
    }

    private void onLevelSelected(int levelId) {
        // Handle level selection here
        System.out.println("Level selected: " + levelId);
        SceneHandler.setScene(Game.newGame(levelId));
        Stocker.getInstance().setLevel(levelId);
    }

}
