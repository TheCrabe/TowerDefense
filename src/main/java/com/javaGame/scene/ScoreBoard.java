package com.javaGame.scene;

import com.javaGame.handler.GameLoop;
import com.javaGame.handler.LevelHandler;
import com.javaGame.handler.SceneHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Glow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ScoreBoard extends Group {
    public ScoreBoard() {
        // create background ImageView
        ImageView backgroundImageView = new ImageView("/menu/images/bg2.png");
        backgroundImageView.setFitWidth(LevelHandler.getWIDTH());
        backgroundImageView.setFitHeight(LevelHandler.getHEIGHT());

        // create ScoreBoard Title
        Text scoreBoardLabel = createText("SCOREBOARD");

        // create ScoreBoard Scores
        ListView<String> scoresListView = new ListView<String>();
        ObservableList<String> scoresObservableList = readScoresFromXML();
        scoresListView.setItems(scoresObservableList);
        scoresListView.setPrefWidth(100);
        scoresListView.setPrefHeight(250);

        // create ScoreBoard Buttons
        StackPane retryButton = createButton("RETRY ?");
        retryButton.setOnMouseClicked(event -> {
            SceneHandler.setScene(Game.newGame(1));
        });

        StackPane quitButton = createButton("QUIT");
        quitButton.setOnMouseClicked(event -> {
            // GameLoop.getInstance().stop();
            GameLoop.getInstance().reset();
            SceneHandler.setScene(new Scene(new StartMenu().getRoot()));
        });

        HBox scoreBoardButtons = new HBox(10);
        scoreBoardButtons.getChildren().addAll(retryButton, quitButton);

        // Assemble ScoreBoard Parts
        VBox scoreBoardVBox = new VBox(20);
        scoreBoardVBox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        scoreBoardVBox.setOpacity(0.9);
        scoreBoardVBox.setAlignment(Pos.CENTER);
        scoreBoardVBox.setLayoutX(((double)LevelHandler.getWIDTH() - 320) / 2);
        scoreBoardVBox.setLayoutY(((double)LevelHandler.getHEIGHT() - 344) / 2);
        scoreBoardVBox.getChildren().addAll(scoreBoardLabel, scoresListView, scoreBoardButtons);

        this.getChildren().addAll(backgroundImageView, scoreBoardVBox);
    }

    private Text createText(String newText) {
        Text text = new Text(newText);
        text.setFont(new Font("Arial", 24));
        text.setStroke(Color.BLACK);
        text.setStrokeWidth(2);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(3.0);
        dropShadow.setOffsetX(3.0);
        dropShadow.setOffsetY(3.0);
        dropShadow.setColor(Color.color(0.4, 0.4, 0.4));
        text.setEffect(dropShadow);

        return text;
    }

    public StackPane createButton(String title) {
        StackPane button = new StackPane();

        // Set the button text and style
        Text text = new Text(title);
        text.setFont(Font.font(20));
        text.setFill(Color.WHITE);

        // Create a background rectangle for the button
        Rectangle bg = new Rectangle(150, 30);
        bg.setOpacity(0.6);
        bg.setFill(Color.BLACK);
        bg.setEffect(new GaussianBlur(3.5));

        button.setAlignment(Pos.CENTER);
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
    private ObservableList<String> readScoresFromXML() {
        ObservableList<String> scores = FXCollections.observableArrayList();
        try {
            File xmlFile = new File("src/main/java/com/javaGame/lvl/ScoreBoard.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("score");

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    String player = eElement.getElementsByTagName("player").item(0).getTextContent();
                    String points = eElement.getElementsByTagName("points").item(0).getTextContent();
                    scores.add(points + " pts " + player);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return scores;
    }
}