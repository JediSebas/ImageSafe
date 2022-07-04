package com.jedisebas.imagesafe;

import static android.os.Build.VERSION.SDK_INT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private String login;
    private String password;
    private UserDao userDao;
    private ImageDao imageDao;
    private SharedPreferences sessionPrefs;

    final ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    // Handle the Intent
                    List<Uri> uris = new ArrayList<>();

                    if (data != null) {
                        ClipData clipData = data.getClipData();

                        if (clipData != null) {
                            //multiple images selected
                            for (int i = 0; i < clipData.getItemCount(); i++) {
                                uris.add(clipData.getItemAt(i).getUri());
                            }
                        } else {
                            //single image selected
                            uris.add(data.getData());
                        }
                    }

                    for (int i=0; i<uris.size(); i++) {
                        String pathFile = PathUtil.getPath(getBaseContext(), uris.get(i));
                        if (pathFile != null) {
                            File firstFile = new File(pathFile);

                            String newPathFile = getDir(login) + firstFile.getName();

                            File secondFile = new File(newPathFile);

                            XorCipher xor = new XorCipher();
                            byte[] encryptedByteArray = xor.getEncryptedByteArray(firstFile, password);
                            xor.writeFile(encryptedByteArray, firstFile);

                            if (firstFile.renameTo(secondFile)) {
                                Log.println(Log.ASSERT, "file", "File moved");

                                new Thread(() -> {
                                    int id = userDao.findIdByLogin(login);
                                    imageDao.insertAll(new Image(id, newPathFile));
                                }).start();

                                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
                            } else {
                                Log.println(Log.ASSERT, "file", "Moved error");
                            }

                            Log.println(Log.ASSERT, "uri", pathFile);
                            Log.println(Log.ASSERT, "uri", newPathFile);
                            Log.println(Log.ASSERT, "dir", Environment.getExternalStorageDirectory().getAbsolutePath());
                        }
                    }
                }
            });

    @SuppressLint("IntentReset")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences themePrefs = getSharedPreferences(Session.THEME_PREFS, Context.MODE_PRIVATE);
        String theme = themePrefs.getString(Session.THEME_KEY, "default");

        if ("default".equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if ("dark".equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else if ("light".equals(theme)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_home);

        final Button browseBtn = findViewById(R.id.browseBtn);
        final Button addPhotosBtn = findViewById(R.id.addBtn);
        final Button logoutBtn = findViewById(R.id.logoutBtn);
        sessionPrefs = getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);
        login = sessionPrefs.getString(Session.LOGIN_KEY, null);
        password = sessionPrefs.getString(Session.PASSWORD_KEY, null);

        SafeDatabase db = SafeDatabase.getInstance(this);
        userDao = db.userDao();
        imageDao = db.imageDao();

        browseBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            } else {
                Intent intent = new Intent(HomeActivity.this, BrowseActivity.class);
                startActivity(intent);
            }
        });

        addPhotosBtn.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            } else {
                Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
                getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                getIntent.setType("image/*");

                Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                pickIntent.setType("image/*");

                Intent chooserIntent = Intent.createChooser(getIntent, getString(R.string.select_image));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

                mStartForResult.launch(chooserIntent);
            }
        });

        logoutBtn.setOnClickListener(view -> {

            SharedPreferences.Editor editor = sessionPrefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        setTitle(getString(R.string.hello) + " " + login);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (sessionPrefs.getString(Session.LOGIN_KEY, null) == null &&
                sessionPrefs.getString(Session.PASSWORD_KEY, null) == null) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent;
            intent = new Intent(HomeActivity.this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    private void checkPermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private String getDir(String login) {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/.secret_safe_" + login + "/";
    }
}