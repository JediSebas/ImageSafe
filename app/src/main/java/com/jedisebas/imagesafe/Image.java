package com.jedisebas.imagesafe;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "image")
public class Image {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userid")
    public int userid;

    public Image(int id, int userid) {
        this.id = id;
        this.userid = userid;
    }

    public Image(int userid) {
        this.userid = userid;
    }

    public Image(Image image) {
        this.id = image.id;
        this.userid = image.userid;
    }

    public Image() {

    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    @NonNull
    @Override
    public String toString() {
        return "Image {" +
                "\nid = " + id +
                "\nuserid = " + userid +
                "\n}";
    }
}
