package com.jedisebas.imagesafe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    static GridItem gridItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        final ImageView imageView = findViewById(R.id.photoIv);
        final Button removeBtn = findViewById(R.id.removeBtn);

        SharedPreferences sessionPrefs = getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);
        String password = sessionPrefs.getString(Session.PASSWORD_KEY, null);

        SafeDatabase db = SafeDatabase.getInstance(this);
        ImageDao imageDao = db.imageDao();

        XorCipher xor = new XorCipher();
        File file = new File(gridItem.getPathFile());
        byte[] encryptedByteArray = xor.getEncryptedByteArray(file, password);

        imageView.setImageBitmap(BitmapFactory.decodeByteArray(encryptedByteArray, 0, encryptedByteArray.length));

        removeBtn.setOnClickListener(view -> {

            String root = Environment.getExternalStorageDirectory() + "/DCIM/OutOfSafe";

            File newDir = new File(root);
            if (newDir.mkdir()) {
                Log.println(Log.ASSERT, "mkdir", "directory created");
            }

            File firstFile = new File(gridItem.getPathFile());
            File secondFile = new File(root + "/" + firstFile.getName());

            byte[] encryptedByteArrayTwo = xor.getEncryptedByteArray(firstFile, password);
            xor.writeFile(encryptedByteArrayTwo, firstFile);

            if (firstFile.renameTo(secondFile)) {
                Log.println(Log.ASSERT, "file", "File moved");

                new Thread(() -> {
                    Image image = imageDao.getImageByPath(gridItem.getPathFile());
                    imageDao.delete(image);
                }).start();

                Toast.makeText(this, getString(R.string.success), Toast.LENGTH_SHORT).show();
            } else {
                Log.println(Log.ASSERT, "file", "Moved error");
            }
        });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
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
}