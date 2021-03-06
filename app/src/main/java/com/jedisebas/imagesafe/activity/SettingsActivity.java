package com.jedisebas.imagesafe.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.jedisebas.imagesafe.R;
import com.jedisebas.imagesafe.util.SafeDatabase;
import com.jedisebas.imagesafe.dao.ImageDao;
import com.jedisebas.imagesafe.dao.UserDao;
import com.jedisebas.imagesafe.entity.Image;
import com.jedisebas.imagesafe.entity.User;
import com.jedisebas.imagesafe.util.ThemeUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        // Respond to the action bar's Up/Home button
        if (item.getItemId() == android.R.id.home) {
            super.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(final Bundle savedInstanceState, final String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            final ListPreference listPreference = findPreference("theme");
            final EditTextPreference editTextPreference = findPreference("account_delete");
            final EditTextPreference recoveryPreference = findPreference("recovery");

            if (listPreference != null) {
                listPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                    final SharedPreferences themePrefs = requireContext().getSharedPreferences(getString(R.string.THEME_PREFS), Context.MODE_PRIVATE);
                    final SharedPreferences.Editor editor = themePrefs.edit();
                    editor.putString(getString(R.string.theme_key), String.valueOf(newValue));
                    editor.apply();

                    new ThemeUtil(String.valueOf(newValue));

                    return true;
                });
            }

            if (editTextPreference != null) {
                editTextPreference.setText("");
                editTextPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                    deleteAccount(newValue);
                    return true;
                });
            }

            if (recoveryPreference != null) {

                final SharedPreferences sessionPrefs = requireContext().getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
                recoveryPreference.setText(sessionPrefs.getString(getString(R.string.email_key), ""));

                recoveryPreference.setOnPreferenceChangeListener((preference, newValue) -> {

                    final SafeDatabase db = SafeDatabase.getInstance(requireContext());
                    final UserDao userDao = db.userDao();

                    new Thread(() -> userDao.updateEmail(String.valueOf(newValue), sessionPrefs.getString(getString(R.string.login_key), null))).start();

                    final SharedPreferences.Editor editor = sessionPrefs.edit();
                    editor.putString(getString(R.string.email_key), String.valueOf(newValue));
                    editor.apply();

                    return true;
                });
            }
        }

        private void deleteAccount(final Object newValue) {

            final SharedPreferences sessionPrefs = requireContext().getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
            final String login = sessionPrefs.getString(getString(R.string.login_key), null);
            final String password = sessionPrefs.getString(getString(R.string.password_key), null);

            final SafeDatabase db = SafeDatabase.getInstance(requireContext());
            final UserDao userDao = db.userDao();
            final ImageDao imageDao = db.imageDao();

            if (password.equals(newValue)) {
                Toast.makeText(requireContext(), getString(R.string.deleting), Toast.LENGTH_LONG).show();

                final SharedPreferences.Editor editor = sessionPrefs.edit();
                editor.clear();
                editor.apply();

                Log.println(Log.ASSERT, "prefs",
                        sessionPrefs.getString(getString(R.string.login_key), null) + " " +
                                sessionPrefs.getString(getString(R.string.password_key), null));

                new Thread(() -> {
                    final User user = userDao.findByLogin(login);
                    final List<Image> imageList = imageDao.getImageByUserId(user.getId());

                    for (final Image image: imageList) {
                        try {
                            Files.delete(Paths.get(image.getFile()));
                            imageDao.delete(image);
                        } catch (final IOException e) {
                            e.printStackTrace();
                        }
                    }

                    try {
                        Files.delete(Paths.get(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/.secret_safe_" + login));
                    } catch (final IOException e) {
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