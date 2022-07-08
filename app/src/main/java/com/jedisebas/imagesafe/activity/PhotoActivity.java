package com.jedisebas.imagesafe.activity;

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

import com.jedisebas.imagesafe.R;
import com.jedisebas.imagesafe.util.SafeDatabase;
import com.jedisebas.imagesafe.util.XorCipher;
import com.jedisebas.imagesafe.dao.ImageDao;
import com.jedisebas.imagesafe.entity.Image;

import java.io.File;

public class PhotoActivity extends AppCompatActivity {

    static GridItem gridItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        final ImageView imageView = findViewById(R.id.photoIv);
        final Button removeBtn = findViewById(R.id.removeBtn);

        final SharedPreferences sessionPrefs = getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        final String password = sessionPrefs.getString(getString(R.string.password_key), null);

        final SafeDatabase db = SafeDatabase.getInstance(this);
        final ImageDao imageDao = db.imageDao();

        final XorCipher xor = new XorCipher();
        final File file = new File(gridItem.getPathFile());
        final byte[] encryptedByteArray = xor.getEncryptedByteArray(file, password);

        imageView.setImageBitmap(BitmapFactory.decodeByteArray(encryptedByteArray, 0, encryptedByteArray.length));

        removeBtn.setOnClickListener(view -> {
            final String root = Environment.getExternalStorageDirectory() + "/DCIM/OutOfSafe";

            final File newDir = new File(root);
            if (newDir.mkdir()) {
                Log.println(Log.ASSERT, "mkdir", "directory created");
            }

            final File firstFile = new File(gridItem.getPathFile());
            final File secondFile = new File(addFileToDirectory(root, firstFile.getName()));

            final byte[] encryptedByteArrayTwo = xor.getEncryptedByteArray(firstFile, password);
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

        final ActionBar actionBar = getSupportActionBar();
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

    private String addFileToDirectory(String directory, String fileName) {
        return directory + "/" + fileName;
    }
}