package com.jedisebas.imagesafe;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface ImageDao {

    @Query("SELECT * FROM image")
    List<Image> getAll();

    @Query("SELECT * FROM image WHERE userid = :userid")
    List<Image> getImageByUserId(int userid);

    @Query("SELECT * FROM image WHERE file = :pathFile")
    Image getImageByPath(String pathFile);

    @Insert
    void insertAll(Image... images);

    @Delete
    void delete(Image image);
}
