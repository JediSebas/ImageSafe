package com.jedisebas.imagesafe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GridItem {

    private Bitmap image;
    private String pathFile;

    GridItem(Bitmap image, String pathFile) {
        this.image = image;
        this.pathFile = pathFile;
    }

    GridItem(String pathFile) {
        this.pathFile = pathFile;
        image = BitmapFactory.decodeFile(pathFile);
    }

    GridItem() {
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setPathFile(String pathFile) {
        this.pathFile = pathFile;
    }

    public String getPathFile() {
        return pathFile;
    }
}
