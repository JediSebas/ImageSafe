package com.jedisebas.imagesafe.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.jedisebas.imagesafe.entity.User;

import java.util.List;

@Dao
public interface UserDao {

    @Query("SELECT * FROM user")
    List<User> getAll();

    @Query("SELECT * FROM user WHERE login = :login")
    User findByLogin(final String login);

    @Query("SELECT id FROM user WHERE login = :login")
    int findIdByLogin(final String login);

    @Query("UPDATE User SET email = :email WHERE login = :login")
    void updateEmail(final String email, final String login);

    @Insert
    void insertAll(final User... users);

    @Delete
    void delete(final User user);
}
