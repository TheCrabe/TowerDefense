package com.javaGame.handler;

import com.javaGame.Cell.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.io.File;
import java.util.Random;

import com.javaGame.monster.Goblin;
import com.javaGame.scene.Game;
import com.javaGame.tower.ArrowTower;
import javafx.animation.PathTransition;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Path;
import javafx.util.Duration;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.util.Arrays;

public class LevelHandler {
    // Constants from Game.java
    private static final int TILE_SIZE = 40;
    private static final int WIDTH = 800;
    private static final int HEIGHT = 450;
    private static final int COLUMNS = WIDTH / TILE_SIZE;
    private static final int ROWS = HEIGHT / TILE_SIZE;
    private static Pane actualRoot;

    // 2D array of Cell objects for coordinates
    private final Cell[][] coordinates = new Cell[ROWS][COLUMNS];


    // Enemy path
    private  Double[] spawnCoordiante;
    private Double[] castleCoordinate;
    private Polyline enemyPath;private PathTransition transition;

    // Singleton instance
    private static LevelHandler instance = null;
    private GameEconomy gameEconomy;

    // Private constructor
    private LevelHandler() {}

    // Public method to get the instance
    public static LevelHandler getInstance() {
        if (instance == null) {
            instance = new LevelHandler();
        }
        return instance;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    public Pane createField(int level) {
        Pane root = new Pane();

        String[] lines = loadLevel("lvl" + level).split("\n");

        for (int y = 0; y < lines.length; y++) {
            for (int x = 0; x < lines[y].length(); x++) {
                char ch = lines[y].charAt(x);

                switch (ch) {
                    case '0':
                        coordinates[y][x] = new NeutralCell(new int[][]{{x, y}});
                        break;
                    case '1':
                        coordinates[y][x] = new EnemyPath(new int[][]{{x, y}});
                        break;
                    case '2':
                        coordinates[y][x] = new TowerSlot(new int[][]{{x, y}});
                        break;
                    case '3':
                        coordinates[y][x] = new Castle(new int[][]{{x, y}});
                        castleCoordinate = new Double[]{(double) x, (double) y};
                        break;
                    case '4':
                        coordinates[y][x] = new EnemyPath(new int[][]{{x, y}});
                        spawnCoordiante = new Double[]{(double) x, (double) y};
                        break;
                }

                ImageView cell = coordinates[y][x];
                cell.setImage(coordinates[y][x].getTexture().getImage());
                coordinates[y][x].setImgView(cell);

                cell.setX(x * TILE_SIZE);
                cell.setY(y * TILE_SIZE);
                cell.setFitHeight(TILE_SIZE);
                cell.setFitWidth(TILE_SIZE);

                int finalX = x;
                int finalY = y;
                cell.setOnMouseClicked(e -> handleCellClick(e, finalX, finalY));
                root.getChildren().add(cell);
            }

        }
        addDecorations(root);
        setPathPattern();
        root.getChildren().add(this.enemyPath);
        /*root.getChildren().add(test());*/
        actualRoot = root;
        Stocker.getInstance().setLevelPane(root);
        Stocker.getInstance().setCoordinates(coordinates);
        return root;
    }

    private void handleCellClick(MouseEvent event, int x, int y) {
        final Cell cell = coordinates[y][x];
        System.out.println(Arrays.deepToString(cell.getCoordinates()) + "de type" + cell.getType());
        if(cell.getType() == 3){
            nextLevel();
            Stocker.getInstance().reset();
            SceneHandler.setScene(Game.newGame(Stocker.getInstance().getLevel()));

        }
        if (cell.getType() == 2 && cell.getTower() == null) {
            GameLoop.getInstance().spawnTower(coordinates[y][x], x, y, TILE_SIZE);
        }
    }
    public String loadLevel(String levelId) {
        try {
            File fxmlFile = new File("src/main/java/com/javaGame/lvl/level.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(fxmlFile);

            // Normalize the XML structure
            doc.getDocumentElement().normalize();

            // Here we fetch the "Level" nodes
            NodeList nList = doc.getElementsByTagName("Level");

            for (int i = 0; i < nList.getLength(); i++) {
                Element elem = (Element) nList.item(i);

                // Check if the id attribute matches the levelId we are looking for
                if (elem.getAttribute("id").equals(levelId)) {
                    return elem.getTextContent().trim().replace(" ", "\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null; // or throw an exception
    }

    public static void nextLevel() {
        Stocker.getInstance().setLevel(Stocker.getInstance().getLevel() + 1);
    }

    public Polyline createEnemyPath(Double[] newSpawnCoordinate) {
        // Create polygon object
        Polyline newEnemyPath = new Polyline();

        // Coordinates for exploring and add Castle coordinates
        double xPath = newSpawnCoordinate[0];
        double yPath = newSpawnCoordinate[1];
        double oldXPath = 0;
        double oldYPath = 0;
        newEnemyPath.getPoints().addAll(xPath * TILE_SIZE + (TILE_SIZE / 2), yPath * TILE_SIZE + (TILE_SIZE / 2));

        // Explore coordinate to add next path positions
        while ((xPath >= 0 && xPath < COLUMNS) && (yPath >= 0 && yPath < ROWS)) {
            double tmpX = oldXPath;
            double tmpY = oldYPath;
            if (xPath - 1 >= 0  &&  xPath - 1 != oldXPath  &&  coordinates[(int)yPath][(int)xPath - 1].getType() == 1) {
                oldXPath = xPath--;
                oldYPath = yPath;
            }
            else if (xPath + 1 < COLUMNS  &&   xPath + 1 != oldXPath  &&  coordinates[(int)yPath][(int)xPath + 1].getType() == 1) {
                oldXPath = xPath++;
                oldYPath = yPath;
            }
            else if (yPath - 1 >= 0  &&  yPath - 1 != oldYPath  &&  coordinates[(int)yPath - 1][(int)xPath].getType() == 1) {
                oldXPath = xPath;
                oldYPath = yPath--;
            }
            else if (yPath + 1 < ROWS  &&  yPath + 1 != oldYPath  &&  coordinates[(int)yPath + 1][(int)xPath].getType() == 1) {
                oldXPath = xPath;
                oldYPath = yPath++;
            }
            if (tmpX == oldXPath && tmpY == oldYPath) { return newEnemyPath; }
            else { newEnemyPath.getPoints().addAll(xPath * TILE_SIZE + (TILE_SIZE / 2), yPath * TILE_SIZE + (TILE_SIZE / 2)); }
        }
        return newEnemyPath;
    }

    public void setPathPattern() {
        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (coordinates[y][x] != null && coordinates[y][x].getType() == 1) {
                    ((EnemyPath)coordinates[y][x]).setPattern(getPattern(x, y));
                    String pattern = ((EnemyPath)coordinates[y][x]).getPattern();
                    System.out.println("src/main/resources/game/assets/world/path"+pattern+".png");
                    coordinates[y][x].getImgView().setImage(new Image("/game/assets/world/path" + pattern + ".png"));

                    System.out.println("Cell at (" + x + "," + y + ") is " + pattern);
                }
            }
        }
        this.enemyPath = createEnemyPath(this.spawnCoordiante);
        this.enemyPath.setStroke(Color.TRANSPARENT);
        Stocker.getInstance().setPath(enemyPath);

    }

    private String getPattern(int x, int y) {
        boolean up      =   y > 0             &&    coordinates[y-1][x] != null     &&  coordinates[y-1][x].getType() == 1;
        boolean down    =   y < ROWS-1        &&    coordinates[y+1][x] != null     &&  coordinates[y+1][x].getType() == 1;
        boolean left    =   x > 0             &&    coordinates[y][x-1] != null     &&  coordinates[y][x-1].getType() == 1;
        boolean right   =   x < COLUMNS-1     &&    coordinates[y][x+1] != null     &&  coordinates[y][x+1].getType() == 1;

        if (up && down && !left && !right) {
            return "Vertical";
        } else if (!up && !down && left && right) {
            return "Horizontal";
        } else if (down && right && !up && !left) {
            return "BasDroite";
        } else if (down && left && !up && !right) {
            return "BasGauche";
        } else if (up && right && !down && !left) {
            return "HautDroite";
        } else if (up && left && !down && !right) {
            return "HautGauche";
        } else {
            return "Horizontal";
        }
    }
    public void addDecorations(Pane root) {
        File decorFolder = new File("src/main/resources/game/assets/decoration");
        File[] decorFiles = decorFolder.listFiles((dir, name) -> name.endsWith(".png") || name.endsWith(".jpg"));

        // Vérifiez si le dossier contient des images
        if (decorFiles == null || decorFiles.length == 0) {
            return;
        }

        Random rand = new Random();

        for (int y = 0; y < ROWS; y++) {
            for (int x = 0; x < COLUMNS; x++) {
                if (coordinates[y][x] != null) {
                    ImageView decorImageView = new ImageView();
                    if (coordinates[y][x].getType() == 3) {
                        decorImageView.setImage(new Image("/game/assets/world/castle.png"));
                        // Réajuster la position
                        decorImageView.setX(x * TILE_SIZE - TILE_SIZE * 0.5); // Ajustez ces valeurs
                        decorImageView.setY(y * TILE_SIZE - TILE_SIZE * 3);

                        decorImageView.setFitHeight(TILE_SIZE * 4);
                        decorImageView.setFitWidth(TILE_SIZE * 3);

                        // Stocker pour les ajouter plus tard
                        decorImageView.toBack();
                        root.getChildren().add(decorImageView);
                        decorImageView.setMouseTransparent(true);
                        continue;
                    }
                    if (coordinates[y][x].getType() == 0 && rand.nextInt(5) == 0) { // 1 chance sur 5 pour les cellules de type 0
                        File randomDecorFile = decorFiles[rand.nextInt(decorFiles.length)];
                        decorImageView.setImage(new Image(randomDecorFile.toURI().toString()));
                    } else if (coordinates[y][x].getType() == 2) { // Condition spéciale pour les cellules de type 2
                        decorImageView.setImage(new Image("/game/assets/world/PlaceForTower2.png"));
                    }
                    else {
                        continue; // Passez a la cellule suivante si aucune des conditions n'est remplie
                    }
                    decorImageView.setX(x * TILE_SIZE);
                    decorImageView.setY(y * TILE_SIZE);
                    decorImageView.setFitHeight(TILE_SIZE);
                    decorImageView.setFitWidth(TILE_SIZE);
                    decorImageView.setMouseTransparent(true);

                    root.getChildren().add(decorImageView);
                }
            }
        }
    }
    public Cell[][] getCoordinates() {
        return coordinates;
    }
}



