package com.jedisebas.imagesafe;

public abstract class Session {

    public static final String SHARED_PREFS = "SESSION_PREFS";
    public static final String LOGIN_KEY = "login_key";
    public static final String PASSWORD_KEY = "password_key";

    private Session() {
    }
}
