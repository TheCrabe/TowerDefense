package com.javaGame.Cell;

import com.javaGame.tower.ATower;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

public interface ICell {
    int getType();
    int[][] getCoordinates();
    ImageView getTexture();
    ImageView getImgView();
    ATower getTower();

    void setTexture(String texture);
    void setImgView(ImageView imgView);
    void setTower(ATower tower);
}