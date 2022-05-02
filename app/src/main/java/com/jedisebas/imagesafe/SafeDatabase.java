package com.jedisebas.imagesafe;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Image.class}, version = 1)
public abstract class SafeDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract ImageDao imageDao();
}
