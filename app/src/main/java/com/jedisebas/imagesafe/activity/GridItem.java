package com.jedisebas.imagesafe.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class GridItem {

    private Bitmap image;
    private String pathFile;

    GridItem(final Bitmap image, final String pathFile) {
        this.image = image;
        this.pathFile = pathFile;
    }

    GridItem(final String pathFile) {
        this.pathFile = pathFile;
        image = BitmapFactory.decodeFile(pathFile);
    }

    GridItem() {
    }

    public void setImage(final Bitmap image) {
        this.image = image;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setPathFile(final String pathFile) {
        this.pathFile = pathFile;
    }

    public String getPathFile() {
        return pathFile;
    }
}
