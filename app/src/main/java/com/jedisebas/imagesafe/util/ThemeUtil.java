package com.jedisebas.imagesafe.util;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

import com.jedisebas.imagesafe.R;

public class ThemeUtil {

    public ThemeUtil(final Context context) {
        final SharedPreferences themePrefs = context.getSharedPreferences(context.getString(R.string.THEME_PREFS), Context.MODE_PRIVATE);
        final String theme = themePrefs.getString(context.getString(R.string.theme_key), "default");

        checkTheme(theme);
    }

    public ThemeUtil(final String value) {
        checkTheme(value);
    }

    void checkTheme(final String value) {
        if ("default".equals(value)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if ("dark".equals(value)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if ("light".equals(value)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
}
