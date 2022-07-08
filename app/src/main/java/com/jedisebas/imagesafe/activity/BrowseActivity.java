package com.jedisebas.imagesafe.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.GridView;
import android.widget.ProgressBar;

import com.jedisebas.imagesafe.R;
import com.jedisebas.imagesafe.util.SafeDatabase;
import com.jedisebas.imagesafe.dao.ImageDao;
import com.jedisebas.imagesafe.dao.UserDao;
import com.jedisebas.imagesafe.entity.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        final GridView gridView = findViewById(R.id.gridLayout);
        final ProgressBar loading = findViewById(R.id.loadingBrowse);

        final SafeDatabase db = SafeDatabase.getInstance(this);
        final UserDao userDao = db.userDao();
        final ImageDao imageDao = db.imageDao();

        final SharedPreferences sessionPrefs = getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        final String login = sessionPrefs.getString(getString(R.string.login_key), null);

        final List<GridItem> gridItemList = new ArrayList<>();

        new Thread(() -> {
            final int id = userDao.findIdByLogin(login);
            final List<Image> imageList = imageDao.getImageByUserId(id);

            for (int i = 0; i < imageList.size(); i++) {
                final String pathFile = imageList.get(i).getFile();
                final File file = new File(pathFile);
                if (file.exists()) {
                    gridItemList.add(new GridItem(pathFile));
                } else {
                    imageDao.delete(imageList.get(i));
                }
            }

            runOnUiThread(() -> {
                final GridViewAdapter adapter = new GridViewAdapter(this, 0, gridItemList);
                gridView.setAdapter(adapter);
                loading.setVisibility(View.GONE);
            });
        }).start();

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            PhotoActivity.gridItem = (GridItem) adapterView.getItemAtPosition(i);
            startActivity(new Intent(this, PhotoActivity.class));
        });

        setTitle(getString(R.string.browse));
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
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
}