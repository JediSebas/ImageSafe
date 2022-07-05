package com.jedisebas.imagesafe;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference listPreference = findPreference("theme");
            EditTextPreference editTextPreference = findPreference("account_delete");
            EditTextPreference recoveryPreference = findPreference("recovery");

            if (listPreference != null) {
                listPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                    SharedPreferences themePrefs = requireContext().getSharedPreferences(Session.THEME_PREFS, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = themePrefs.edit();
                    editor.putString(Session.THEME_KEY, String.valueOf(newValue));
                    editor.apply();

                    if ("default".equals(newValue)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                    } else if ("dark".equals(newValue)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    } else if ("light".equals(newValue)) {
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    }

                    return true;
                });
            }

            if (editTextPreference != null) {
                editTextPreference.setText("");
                editTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                    editTextPreference.setText("");

                    deleteAccount(newValue);

                    editTextPreference.setText("");

                    return true;
                });
            }

            if (recoveryPreference != null) {

                SharedPreferences sessionPrefs = requireContext().getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);
                recoveryPreference.setText(sessionPrefs.getString(Session.EMAIL_KEY, ""));

                recoveryPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                    SafeDatabase db = SafeDatabase.getInstance(requireContext());
                    UserDao userDao = db.userDao();

                    new Thread(() -> userDao.updateEmail(String.valueOf(newValue), sessionPrefs.getString(Session.LOGIN_KEY, null))).start();

                    SharedPreferences.Editor editor = sessionPrefs.edit();
                    editor.putString(Session.EMAIL_KEY, String.valueOf(newValue));
                    editor.apply();

                    return true;
                });
            }
        }

        private void deleteAccount(Object newValue) {

            SharedPreferences sessionPrefs = requireContext().getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);
            String login = sessionPrefs.getString(Session.LOGIN_KEY, null);
            String password = sessionPrefs.getString(Session.PASSWORD_KEY, null);

            SafeDatabase db = SafeDatabase.getInstance(requireContext());
            UserDao userDao = db.userDao();
            ImageDao imageDao = db.imageDao();

            if (password.equals(newValue)) {
                Toast.makeText(requireContext(), getString(R.string.deleting), Toast.LENGTH_LONG).show();

                SharedPreferences.Editor editor = sessionPrefs.edit();
                editor.clear();
                editor.apply();

                Log.println(Log.ASSERT, "prefs",
                        sessionPrefs.getString(Session.LOGIN_KEY, null) + " " +
                                sessionPrefs.getString(Session.PASSWORD_KEY, null));

                new Thread(() -> {
                    User user = userDao.findByLogin(login);
                    List<Image> imageList = imageDao.getImageByUserId(user.getId());

                    for (int i=0; i<imageList.size(); i++) {
                        try {
                            Files.delete(Paths.get(imageList.get(i).getFile()));
                            imageDao.delete(imageList.get(i));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Files.delete(Paths.get(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/.secret_safe_" + login));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    userDao.delete(user);
                    Log.println(Log.ASSERT, "users", String.valueOf(userDao.getAll()));

                }).start();

            } else {
                Toast.makeText(requireContext(), getString(R.string.wrong_pass), Toast.LENGTH_LONG).show();
            }
        }
    }
}