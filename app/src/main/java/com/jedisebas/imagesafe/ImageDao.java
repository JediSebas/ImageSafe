package com.jedisebas.imagesafe;

import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ImageDao {

    @Query("SELECT * FROM image")
    List<Image> getAll();
}
