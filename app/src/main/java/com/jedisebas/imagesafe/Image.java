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

    @ColumnInfo(name = "file")
    public String file;

    public Image(int id, int userid, String file) {
        this.id = id;
        this.userid = userid;
        this.file = file;
    }

    public Image(int userid, String file) {
        this.userid = userid;
        this.file = file;
    }

    public Image(Image image) {
        this.id = image.id;
        this.userid = image.userid;
        this.file = image.file;
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

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile() {
        return file;
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
