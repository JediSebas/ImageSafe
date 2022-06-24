package com.jedisebas.imagesafe;

import static android.os.Build.VERSION.SDK_INT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final int IMAGE_PICK_CODE = 1000;
    private String login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button browseBtn = findViewById(R.id.browseBtn);
        final Button addPhotosBtn = findViewById(R.id.addBtn);
        final Button logoutBtn = findViewById(R.id.logoutBtn);
        SharedPreferences sessionPrefs = getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);
        login = sessionPrefs.getString(Session.LOGIN_KEY, null);

        browseBtn.setOnClickListener(view -> checkPermission());

        addPhotosBtn.setOnClickListener(view -> checkPermission());

        logoutBtn.setOnClickListener(view -> {

            SharedPreferences.Editor editor = sessionPrefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        setTitle("Hello " + login);
    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
        } else {
            pickImageFromGallery();
        }
    }

    @SuppressLint("IntentReset")
    private void pickImageFromGallery() {
        Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
        getIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        getIntent.setType("image/*");

        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        pickIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});

        startActivityForResult(chooserIntent, IMAGE_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {

            List<Uri> uris = new ArrayList<>();
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


            for (int i=0; i<uris.size(); i++) {
                String pathFile = PathUtil.getPath(getBaseContext(), uris.get(i));
                if (pathFile != null) {
                    File firstFile = new File(pathFile);

                    String newPathFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/.secret_safe_" + login + "/" + firstFile.getName();

                    File secondFile = new File(newPathFile);

                    if (firstFile.renameTo(secondFile)) {
                        Log.println(Log.ASSERT, "file", "File moved");
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
    }
}