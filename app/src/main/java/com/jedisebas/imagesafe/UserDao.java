package com.jedisebas.imagesafe;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE login = :login")
    User findByLogin(String login);

    @Query("SELECT id FROM user WHERE login = :login")
    int findIdByLogin(String login);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);
}
