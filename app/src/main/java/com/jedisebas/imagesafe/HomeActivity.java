package com.jedisebas.imagesafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Button browseBtn = findViewById(R.id.browseBtn);
        final Button addPhotosBtn = findViewById(R.id.addBtn);
        final Button logoutBtn = findViewById(R.id.logoutBtn);
        SharedPreferences sessionPrefs = getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);

        browseBtn.setOnClickListener(view -> {

        });

        addPhotosBtn.setOnClickListener(view -> {

        });

        logoutBtn.setOnClickListener(view -> {

            SharedPreferences.Editor editor = sessionPrefs.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        setTitle("Hello " + sessionPrefs.getString(Session.LOGIN_KEY, null));
    }
}