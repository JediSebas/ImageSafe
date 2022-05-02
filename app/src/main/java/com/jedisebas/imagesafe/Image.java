package com.jedisebas.imagesafe;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Image {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userid")
    public int userid;

    public Image() {

    }

    public Image(int id, int userid) {
        this.id = id;
        this.userid = userid;
    }
}
