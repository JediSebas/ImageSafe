package com.jedisebas.imagesafe;

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

import java.util.ArrayList;
import java.util.List;

public class BrowseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);

        final GridView gridView = findViewById(R.id.gridLayout);
        final ProgressBar loading = findViewById(R.id.loadingBrowse);

        SafeDatabase db = SafeDatabase.getInstance(this);
        UserDao userDao = db.userDao();
        ImageDao imageDao = db.imageDao();

        SharedPreferences sessionPrefs = getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);
        String login = sessionPrefs.getString(Session.LOGIN_KEY, null);


        List<GridItem> gridItemList = new ArrayList<>();

        new Thread(() -> {
            int id = userDao.findIdByLogin(login);
            List<Image> imageList = imageDao.getImageByUserId(id);

            for (int i=0; i<imageList.size(); i++) {
                gridItemList.add(new GridItem(imageList.get(i).getFile()));
            }

            runOnUiThread(() -> {
                GridViewAdapter adapter = new GridViewAdapter(this, 0, gridItemList);
                gridView.setAdapter(adapter);
                loading.setVisibility(View.GONE);
            });
        }).start();

        gridView.setOnItemClickListener((adapterView, view, i, l) -> {
            Intent intent = new Intent(BrowseActivity.this, PhotoActivity.class);
            PhotoActivity.gridItem = (GridItem) adapterView.getItemAtPosition(i);
            startActivity(intent);
        });

        setTitle("Browse photos");
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