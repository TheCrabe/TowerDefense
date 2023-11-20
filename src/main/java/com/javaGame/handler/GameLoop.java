package com.javaGame.handler;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import com.javaGame.scene.Game;
import com.javaGame.scene.ScoreBoard;
import com.javaGame.scene.StartMenu;
import com.javaGame.tower.*;
import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import com.javaGame.Cell.Cell;
import com.javaGame.monster.AMonster;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

public class GameLoop {
    private static GameLoop instance = null;
    private final AnimationTimer timer;
    private boolean isRunning = false;
    private final long interval = 10000000L; // 10 millisecondes, ce qui équivaut a 100 fois par seconde
    private long lastUpdate = 0;
    private int updateCount = 0;

    private boolean canSpawn = false;
    private boolean isSpawning = false;
    private String SelectedTower = "ArrowTower";
    private static Timer spawnTimer;
    private boolean isSpawningPaused = false;
    private final List<TimerTask> spawnTasks;
    private int currentMonsterIndex = 0;
    private Timeline spawner;
    private Popup currentPopup;

    public static GameLoop getInstance() {
        if (instance == null) {
            instance = new GameLoop();
        }
        return instance;
    }

    public void start() {
        if (!isRunning) {
            timer.start();
            isRunning = true;
        }
    }

    public void stop() {
        if (isRunning) {
            timer.stop();
            isRunning = false;
        }
    }

    private GameLoop() {

        spawnTasks = new ArrayList<>();
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= interval) {
                    update(); // Mettez a jour la logique du jeu ici
                    lastUpdate = now;
                }
            }
        };
    }

    private void update() {
        if (!isRunning) return;
        updateCount++;
        if (updateCount == 500) {
            updateCount = 0;
        }
//        checkPosition();

        updateGame();
        checkAttack();
    }

    private void checkPosition() {
        if(Stocker.getInstance().getGoblins().isEmpty() || Stocker.getInstance().getTowers().isEmpty()){
            return;
        }
        else{
            final List<AMonster> monsters = new ArrayList<>(Stocker.getInstance().getGoblins());
            final List<ATower> towers = new ArrayList<>(Stocker.getInstance().getTowers());
            for (AMonster monster : monsters){
                for (ATower tower : towers){
                    if(monster.getPositionOnScreen().getX() > tower.getPositionOnScreen().getX()){
                        monster.toBack();
                    }else{
                        monster.toFront();
                    }
                }
            }
        }
    }

    private void updateGame() {
        if (isWaveComplete() && !isSpawning) {
            processWaveCompletion();
        }

        if (canSpawn) {
            startSpawningMonsters();
            canSpawn = false;
        }

        checkAttack();
    }

    private boolean isWaveComplete() {
        return Stocker.getInstance().getGoblins().isEmpty();
    }

    private void processWaveCompletion() {
        Stocker.getInstance().getWaveMonsters().clear();
        if (Stocker.getInstance().getWaveNumber() < 2) {
            goToNextWave();
        } else if (Stocker.getInstance().getLevel() < 1) {
            goToNextLevel();
        } else {
            GameLoop.getInstance().stop();
            Platform.runLater(() -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Fin de la Partie");
                dialog.setHeaderText("Bravo, vous avez terminé le jeu !");
                dialog.setContentText("Veuillez entrer votre pseudo:");

                Optional<String> result = dialog.showAndWait();

                result.ifPresent(pseudo -> {

                    saveScore(pseudo, Stocker.getInstance().getScore());

                });
            });
        }
    }
    private void saveScore(String pseudo, GameScore score) {
        try {
            String filePath = "ScoreBoard.xml";
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

            Document document;
            File xmlFile = new File(filePath);
            if (xmlFile.exists()) {
                document = documentBuilder.parse(xmlFile);
                document.getDocumentElement().normalize();
            } else {
                document = documentBuilder.newDocument();
                Element rootElement = document.createElement("scores");
                document.appendChild(rootElement);
            }

            Element newScore = document.createElement("score");
            newScore.setAttribute("pseudo", pseudo);
            newScore.setTextContent(String.valueOf(score));
            document.getDocumentElement().appendChild(newScore);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(xmlFile); 
            transformer.transform(domSource, streamResult);

            System.out.println("Score enregistré avec succès.");

            SceneHandler.setScene(new Scene(new ScoreBoard()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private void goToNextWave() {
        int nextWave = Stocker.getInstance().getWaveNumber() + 1;
        Stocker.getInstance().setWaveNumber(nextWave);
        NewWave(nextWave);
        canSpawn = true;
    }

    private void goToNextLevel() {
        stop();
        Stocker.getInstance().reset();
        int nextLevel = Stocker.getInstance().getLevel() + 1;
        Stocker.getInstance().setLevel(nextLevel);
        SceneHandler.setScene(Game.newGame(nextLevel));
        start();
    }

    private void startSpawningMonsters() {
        if (isSpawningPaused || isSpawning) return;
        isSpawning = true;
        System.out.println("Starting spawn timer");
        System.out.println("Total monsters: " + Stocker.getInstance().getWaveMonsters().size());

        Timeline spawnTimeline = new Timeline();
        int delay = 0;
        int period = 500;

        for (String type : Stocker.getInstance().getWaveMonsters()) {
            KeyFrame keyFrame = new KeyFrame(Duration.millis(delay), e -> {
                if (!isSpawningPaused) {
                    spawnMonster(type);
                }
            });

            spawnTimeline.getKeyFrames().add(keyFrame);
            delay += period;
        }
        spawner = spawnTimeline;
        spawnTimeline.setOnFinished(e -> isSpawning = false);
        spawnTimeline.play();
    }



    public void spawnMonster(String type) {
        AMonster node = getClass(type);

        node.setImage(new Image(node.getTexture()));
        node.setFitHeight(node.getSize());
        node.setFitWidth(node.getSize());

        Polyline polyline = Stocker.getInstance().getPath();
        double pathLength = calculatePolylineLength(polyline);

        double speed = node.getSpeed(); // Vitesse en pixels par seconde, ajustez selon les besoins
        double durationInSeconds = pathLength / speed;

        PathTransition transition = new PathTransition();
        transition.setNode(node);
        transition.setDuration(Duration.seconds(durationInSeconds));
        transition.setPath(new Path(convertPolylineToPathElements(polyline)));
        transition.setCycleCount(1);
        transition.setInterpolator(Interpolator.LINEAR);
        transition.setOnFinished(event -> {
            if(!node.isDead()){
                if (Stocker.getInstance().getPlayerLife().getHealth() > 0 && !node.isDead()) {
                    Stocker.getInstance().getPlayerLife().setHealth(Stocker.getInstance().getPlayerLife().getHealth() - 1);
                    node.die();
                }
                if (Stocker.getInstance().getPlayerLife().getHealth() <= 0) {

                    GameLoop.getInstance().stop();
                    Platform.runLater(() -> {
                        TextInputDialog dialog = new TextInputDialog();
                        dialog.setTitle("Fin de la Partie");
                        dialog.setHeaderText("Bravo, vous avez terminé le jeu !");
                        dialog.setContentText("Veuillez entrer votre pseudo:");

                        Optional<String> result = dialog.showAndWait();

                        result.ifPresent(pseudo -> {
                            writeScoreToXML(pseudo, Stocker.getInstance().getScore().getScore());
                            GameLoop.getInstance().stop();
                            GameLoop.getInstance().reset();
                            SceneHandler.setScene(new Scene(new ScoreBoard()));
                        });
                    });
                    GameLoop.getInstance().stop();
                    GameLoop.getInstance().reset();
                    SceneHandler.setScene(new Scene(new ScoreBoard()));

                }
            }

        });
        transition.play();
        node.setTransition(transition);
        node.translateXProperty().addListener((obs, oldVal, newVal) -> adjustOrder(node));
        Stocker.getInstance().getMonsterTransitions().add(transition);
        currentMonsterIndex++;
        Stocker.getInstance().getLevelPane().getChildren().add(node);
        Stocker.getInstance().addMonster(node);
    }
        private void adjustOrder(ImageView movingImage) {
//        System.out.println("Adjusting order");
            for (ImageView img : Stocker.getInstance().getTowers()) {
                if (img != movingImage && img.getBoundsInParent().intersects(movingImage.getBoundsInParent())) {
                    if (img.getTranslateY() > movingImage.getTranslateY()) {
                        img.toFront();

                    } else {

                        movingImage.toFront();
                    }
                }

            }
        }
    public void pauseSpawning() {
        if(isSpawning){
            spawner.pause();
        }
    }
    public void resumeSpawning() {
        if(isSpawning){
            spawner.play();
        }
    }

    public void adjustMonsterSpeed(AMonster monster, double newSpeed) {
        PathTransition currentTransition = monster.getTransition();

        if (currentTransition == null) {
            return;
        }
        if(monster.isSlow()){
            return;
        }
        monster.setSlow(true);
        double currentProgress = currentTransition.getCurrentTime().toSeconds() / currentTransition.getDuration().toSeconds();
        double newPathLength = calculatePolylineLength(Stocker.getInstance().getPath());
        double newDurationInSeconds = newPathLength / newSpeed;

        PathTransition newTransition = new PathTransition();
        newTransition.setInterpolator(Interpolator.LINEAR);
        newTransition.setNode(monster);
        newTransition.setDuration(Duration.seconds(newDurationInSeconds));
        newTransition.setPath(currentTransition.getPath());
        newTransition.setCycleCount(1);
        monster.setTransition(newTransition);
        currentTransition.stop();
        Stocker.getInstance().getMonsterTransitions().add(newTransition);
        Stocker.getInstance().getMonsterTransitions().remove(currentTransition);
        // Démarrez le nouveau PathTransition au même point de progression
        newTransition.jumpTo(Duration.seconds(newDurationInSeconds * currentProgress));
        newTransition.play();

    }

    public AMonster getClass(String type){
        final String packageName = "com.javaGame.monster."; // Le nom de package où se trouvent vos monstres
        try {
            // Construire le nom complet de la classe
            String className = packageName + type;
            // Récupérer la classe en fonction du nom complet
            Class<?> clazz = Class.forName(className);

            // Assurer que la classe est un sous-type de AMonster
            if (AMonster.class.isAssignableFrom(clazz)) {
                // Créer une nouvelle instance en utilisant le constructeur approprié
                Constructor<?> constructor = clazz.getConstructor(int[][].class);
                AMonster monster = (AMonster) constructor.newInstance((Object) new int[][]{{999, 999}});

                return monster;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private double calculatePolylineLength(Polyline polyline) {
        double length = 0.0;
        List<Double> points = polyline.getPoints();
        for (int i = 2; i < points.size(); i += 2) {
            double deltaX = points.get(i) - points.get(i - 2);
            double deltaY = points.get(i + 1) - points.get(i - 1);
            length += Math.sqrt(deltaX * deltaX + deltaY * deltaY);
        }
        return length;
    }

    private List<PathElement> convertPolylineToPathElements(Polyline polyline) {
        List<PathElement> elements = new ArrayList<>();
        List<Double> points = polyline.getPoints();
        if (!points.isEmpty()) {
            elements.add(new MoveTo(points.get(0), points.get(1)));
        }
        for (int i = 2; i < points.size(); i += 2) {
            elements.add(new LineTo(points.get(i), points.get(i + 1)));
        }
        return elements;
    }

    public void spawnTower(Cell cell, int x, int y, int TILE_SIZE) {
        if (null == cell.getTower()) {
            ATower decorImageView = TowerFactory.createTower(SelectedTower, x, y, TILE_SIZE, cell);
            if(decorImageView.getCost() > Stocker.getInstance().getEconomy().getMoney()){
                System.out.println("Not enough money");
                return;
            }
            Stocker.getInstance().getEconomy().spendMoney(decorImageView.getCost());
            decorImageView.setImage(new Image(decorImageView.getTexture()));
            decorImageView.setCost(decorImageView.getCost() +50);
            System.out.println(cell.getTower());

            decorImageView.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY ) {
                    if (currentPopup != null) {
                        currentPopup.hide();
                    }
                    if (decorImageView.getLevel() < 3) {

                        Popup popup = new Popup();
                        currentPopup = popup;
                        VBox content = new VBox(10);
                        content.setStyle("-fx-background-color: #333; -fx-padding: 20; -fx-border-color: #666; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                        Label label = new Label("Upgrade");
                        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #eee; -fx-text-align: center;");
                        content.getChildren().add(label);
                        Label labelCost = new Label("Cost: " + decorImageView.getCost());
                        System.out.println(decorImageView.getCost());
                        labelCost.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #eee; -fx-text-align: center;");
                        labelCost.setText("Cost: " + decorImageView.getCost());
                        content.getChildren().add(labelCost);

                        Button yesButton = new Button("Oui");
                        yesButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                        if(decorImageView.getCost() > Stocker.getInstance().getEconomy().getMoney()){
                            yesButton.setDisable(true);
                        }else{
                            yesButton.setDisable(false);
                        }
                        yesButton.setOnAction(e -> {
                            Stocker.getInstance().getEconomy().spendMoney(decorImageView.getCost());
                            decorImageView.upgrade();
                            popup.hide();
                        });

                        Button noButton = new Button("Non");
                        noButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                        noButton.setOnAction(e -> popup.hide());

                        HBox buttonBox = new HBox(10, yesButton, noButton);
                        buttonBox.setAlignment(Pos.CENTER);
                        content.getChildren().add(buttonBox);

                        popup.getContent().add(content);

                        double popupWidth = 200;  // Estimation de la largeur du Popup
                        double popupHeight = 100; // Estimation de la hauteur du Popup

                        double X = event.getScreenX();
                        double Y = event.getScreenY();

                        Window window = cell.getScene().getWindow();
                        double windowMaxX = window.getX() + window.getWidth();
                        double windowMaxY = window.getY() + window.getHeight();

                        if (X + popupWidth > windowMaxX) {
                            X = windowMaxX - popupWidth;
                        }
                        if (Y + popupHeight > windowMaxY) {
                            Y = windowMaxY - popupHeight;
                        }

                        popup.show(window, X, Y);
                    }
                }
                if (event.getButton() == MouseButton.SECONDARY){
                    if (currentPopup != null) {
                        currentPopup.hide();
                    }

                        Popup popup = new Popup();
                        currentPopup = popup;
                        VBox content = new VBox(10);
                        content.setStyle("-fx-background-color: #333; -fx-padding: 20; -fx-border-color: #666; -fx-border-width: 2px; -fx-border-radius: 5px; -fx-background-radius: 5px;");
                        Label label = new Label("Vendre ?");
                        label.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #eee; -fx-text-align: center;");
                        content.getChildren().add(label);
                        Label labelCost = new Label("Cost: " + decorImageView.getSellCost());
                        labelCost.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #eee; -fx-text-align: center;");
                        labelCost.setText("Pour: " + decorImageView.getSellCost());
                        content.getChildren().add(labelCost);

                        Button yesButton = new Button("Oui");
                        yesButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-weight: bold;");
                        if(decorImageView.getCost() > Stocker.getInstance().getEconomy().getMoney()){
                            yesButton.setDisable(true);
                        }else{
                            yesButton.setDisable(false);
                        }
                        yesButton.setOnAction(e -> {
                            popup.hide();
                            Platform.runLater(decorImageView::sell);


                        });

                        Button noButton = new Button("Non");
                        noButton.setStyle("-fx-background-color: #f44336; -fx-text-fill: white; -fx-font-weight: bold;");
                        noButton.setOnAction(e -> popup.hide());

                        HBox buttonBox = new HBox(10, yesButton, noButton);
                        buttonBox.setAlignment(Pos.CENTER);
                        content.getChildren().add(buttonBox);

                        popup.getContent().add(content);

                        double popupWidth = 200;  // Estimation de la largeur du Popup
                        double popupHeight = 100; // Estimation de la hauteur du Popup

                        double X = event.getScreenX();
                        double Y = event.getScreenY();

                        Window window = cell.getScene().getWindow();
                        double windowMaxX = window.getX() + window.getWidth();
                        double windowMaxY = window.getY() + window.getHeight();

                        if (X + popupWidth > windowMaxX) {
                            X = windowMaxX - popupWidth;
                        }
                        if (Y + popupHeight > windowMaxY) {
                            Y = windowMaxY - popupHeight;
                        }

                        popup.show(window, X, Y);
                    }


            });


            Stocker.getInstance().getLevelPane().getChildren().add(decorImageView);
            Stocker.getInstance().getTowers().add(decorImageView);

        }
    }

    public void checkAttack() {
        final List<AMonster> goblinsToRemove = new ArrayList<>();
        if (null == Stocker.getInstance().getGoblins() || null == Stocker.getInstance().getTowers()) {
            return;
        }

        for (ATower tower : Stocker.getInstance().getTowers()) {
            for (AMonster goblin : new ArrayList<>(Stocker.getInstance().getGoblins())) { // Créez une copie de la liste pour l'itération
                tower.attackMechant(goblin);
                if (goblin.getHealth() <= 0) {
                    goblinsToRemove.add(goblin);
                }
            }
        }

        if (!goblinsToRemove.isEmpty()) {
            Platform.runLater(() -> Stocker.getInstance().getGoblins().removeAll(goblinsToRemove));
        }
    }

    public void NewWave(Integer waveNumber) {
        GetWaveList("src/main/java/com/javaGame/lvl/wave.xml", Stocker.getInstance().getLevel(), waveNumber.toString());
        canSpawn = true;
    }

    public void GetWaveList(String fichier, Integer niveauId, String vagueNum) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(fichier);

            Element root = document.getDocumentElement();
            NodeList levelList = root.getElementsByTagName("Level");

            for (int i = 0; i < levelList.getLength(); i++) {
                Node level = levelList.item(i);
                if (level.getNodeType() == Node.ELEMENT_NODE) {
                    Element levelElement = (Element) level;
                    String levelId = levelElement.getAttribute("id");

                    if (levelId.equals(niveauId.toString())) {
                        NodeList waveList = levelElement.getElementsByTagName("Wave");
                        for (int j = 0; j < waveList.getLength(); j++) {
                            Node wave = waveList.item(j);
                            if (wave.getNodeType() == Node.ELEMENT_NODE) {
                                Element waveElement = (Element) wave;
                                String waveNumber = waveElement.getAttribute("number");

                                if (waveNumber.equals(vagueNum)) {
                                    System.out.println("Level " + levelId + ", Wave " + waveNumber);

                                    NodeList enemyList = waveElement.getElementsByTagName("Enemy");
                                    for (int k = 0; k < enemyList.getLength(); k++) {
                                        Node enemy = enemyList.item(k);
                                        if (enemy.getNodeType() == Node.ELEMENT_NODE) {
                                            Element enemyElement = (Element) enemy;
                                            String type = enemyElement.getAttribute("name");

                                            for (int l = 0; l < Integer.parseInt(enemyElement.getAttribute("count")); l++) {
                                                Stocker.getInstance().getWaveMonsters().add(type); // Ajouter simplement le type comme chaîne
                                            }
                                            System.out.println(enemyElement.getAttribute("count"));

                                        }
                                    }
                                    return;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setTower(String tower) {
        SelectedTower = tower;
    }

    public void pauseGoblinTransitions() {

        if(isSpawning){
            spawner.pause();
        }
        for (PathTransition transition : Stocker.getInstance().getMonsterTransitions()) {
            if (transition != null) {
                transition.pause();
            }
        }
    }

    public void resumeGoblinTransitions() {
        if(isSpawning){
            spawner.play();
        }
        for (PathTransition transition : Stocker.getInstance().getMonsterTransitions()) {
            if (transition != null && transition.getStatus() == Animation.Status.PAUSED) {
                transition.play();
            }
        }
    }

    public void reset(){
        stop();
        spawner.stop();
        for (PathTransition transition : Stocker.getInstance().getMonsterTransitions()) {
            if (transition != null) {
                transition.stop();
            }
        }
        isSpawning = false;
        isSpawningPaused = false;
        canSpawn = true;
        isRunning = false;
        SelectedTower = "ArrowTower";
        currentMonsterIndex = 0;
        spawnTasks.clear();
        spawnTimer = null;
        Platform.runLater(() -> {
            Stocker.getInstance().getLevelPane().getChildren().clear();
            Stocker.getInstance().getGoblins().clear();
            Stocker.getInstance().getMonsterTransitions().clear();
            Stocker.getInstance().getTowers().clear();
            Stocker.getInstance().getWaveMonsters().clear();
            Stocker.getInstance().setWaveNumber(0);
            Stocker.getInstance().setLevel(1);
            Stocker.getInstance().setChangingLevel(true);
            Stocker.getInstance().setPlayerLife(null);
            Stocker.getInstance().setPath(null);
        });
    }
    public void cleanPopUp(){
        if (currentPopup != null) {
            currentPopup = null;
        }
    }
    public void writeScoreToXML(String player, int points) {
        try {
            File xmlFile = new File("src/main/java/com/javaGame/lvl/ScoreBoard.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);

            Node scores = doc.getFirstChild();

            Element newScore = doc.createElement("score");
            Element playerName = doc.createElement("player");
            playerName.appendChild(doc.createTextNode(player));
            Element scorePoints = doc.createElement("points");
            scorePoints.appendChild(doc.createTextNode(String.valueOf(points)));

            newScore.appendChild(playerName);
            newScore.appendChild(scorePoints);
            scores.appendChild(newScore);

            // Enregistrez les modifications
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(xmlFile);
            transformer.transform(source, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}