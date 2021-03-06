package com.jedisebas.imagesafe.util;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.jedisebas.imagesafe.dao.ImageDao;
import com.jedisebas.imagesafe.dao.UserDao;
import com.jedisebas.imagesafe.entity.Image;
import com.jedisebas.imagesafe.entity.User;

@Database(entities = {User.class, Image.class}, version = 1, exportSchema = false)
public abstract class SafeDatabase extends RoomDatabase {
    private static final String DB_NAME = "safe_db";
    private static SafeDatabase instance;

    public static synchronized SafeDatabase getInstance(final Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), SafeDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration().build();
        }
        return instance;
    }

    public abstract UserDao userDao();
    public abstract ImageDao imageDao();
}
