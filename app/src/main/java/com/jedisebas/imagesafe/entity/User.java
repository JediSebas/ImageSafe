package com.jedisebas.imagesafe.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "login")
    private String login;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "email")
    private String email;

    public User(int id, String login, String password, String email) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.email = email;
    }

    public User(int id, String login, String password) {
        this(id, login, password, null);
    }

    public User(String login, String password) {
        this(0, login, password, null);
    }

    public User(User user) {
        this(user.id, user.login, user.password, user.email);
    }

    public User() {
        this(null, null);
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    @NonNull
    @Override
    public String toString() {
        return "User {" +
                "\nid = " + id +
                "\nlogin = " + login +
                "\npassword = " + password +
                "\nemail = " + email +
                "\n}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!(obj instanceof User)) {
            return false;
        }

        final User user = (User) obj;

        return Objects.equals(this.login, user.login);
    }

    @Override
    public int hashCode() {
        int hash = 17;

        hash = 21 * hash + id;
        hash = 21 * hash + login.hashCode();
        hash = 21 * hash + password.hashCode();
        hash = 21 * hash + email.hashCode();

        return hash;
    }
}
