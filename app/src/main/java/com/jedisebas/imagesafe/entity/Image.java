package com.jedisebas.imagesafe.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "image")
public class Image {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "userid")
    public int userid;

    @ColumnInfo(name = "file")
    public String file;

    public Image(final int id, final int userid, final String file) {
        this.id = id;
        this.userid = userid;
        this.file = file;
    }

    public Image(final int userid, final String file) {
        this(0, userid, file);
    }

    public Image(final Image image) {
        this(image.id, image.userid, image.file);
    }

    public Image() {
        this(0, null);
    }

    public void setId(final int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setUserid(final int userid) {
        this.userid = userid;
    }

    public int getUserid() {
        return userid;
    }

    public void setFile(final String file) {
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
                "\nfile = " + file +
                "\n}";
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof Image)) {
            return false;
        }

        final Image image = (Image) obj;

        return Objects.equals(this.file, image.file);
    }

    @Override
    public int hashCode() {
        int hash = 13;

        hash = 17 * hash + id;
        hash = 17 * hash + userid;
        hash = 17 * hash + file.hashCode();

        return hash;
    }
}
