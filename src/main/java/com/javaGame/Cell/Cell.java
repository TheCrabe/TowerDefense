package com.javaGame.Cell;

import com.javaGame.tower.ATower;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public abstract class Cell extends ImageView implements ICell{
    protected int type;
    protected int[][] coordinates;
    protected String texture;
    protected ImageView imgView;
    protected ATower tower;

    public Cell(int type, int[][] coordinates, String texture) {
        this.type = type;
        this.coordinates = coordinates;
        this.texture = texture;
    }

    public int getType() {
        return type;
    }
    public int[][] getCoordinates() {
        return coordinates;
    }

    public ImageView getTexture() {
        Image img = null;
        try {
            // Attempt to load an image from a file path.
            InputStream is = Files.newInputStream(Paths.get(this.texture));
            img = new Image(is);
            is.close(); // Always close the InputStream to prevent resource leaks.
        } catch (IOException e) {
            // If an error occurs while loading the image, print the stack trace.
            e.printStackTrace();
        }
        // Create an ImageView with the loaded image.
        ImageView imgView = new ImageView(img);
        this.imgView = imgView;
        return imgView;
    }

    public ImageView getImgView() {
        return imgView;
    }
    public void setImgView(ImageView imgView) {
        this.imgView = imgView;
    }

    public void setTexture(String texture) {
        this.texture = texture;
    }

    public void setTower(ATower towerset) {
        tower = towerset;
    }

    public ATower getTower() {
        return tower;
    }
}

