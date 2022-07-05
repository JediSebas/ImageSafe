package com.jedisebas.imagesafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String log;
    private String pass;

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

        setContentView(R.layout.activity_main);

        final EditText loginEt = findViewById(R.id.loginEt);
        final EditText passwordEt = findViewById(R.id.passwordEt);
        final Button signInBtn = findViewById(R.id.signInBtn);
        final ProgressBar progressBar = findViewById(R.id.loading);
        final TextView signUp = findViewById(R.id.signUpTv);
        final TextView recover = findViewById(R.id.recoverTv);
        SharedPreferences sessionPrefs = getSharedPreferences(Session.SHARED_PREFS, Context.MODE_PRIVATE);
        log = sessionPrefs.getString(Session.LOGIN_KEY, null);
        pass = sessionPrefs.getString(Session.PASSWORD_KEY, null);

        signUp.getPaint().setUnderlineText(true);

        SafeDatabase db = SafeDatabase.getInstance(this);
        UserDao userDao = db.userDao();

        signInBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String login = loginEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getText(R.string.not_all), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, getText(R.string.wait_sign_in), Toast.LENGTH_SHORT).show();
                try {

                    new Thread(() -> {
                        User user = userDao.findByLogin(login);

                        if (user != null && user.getPassword().equals(password)) {
                            runOnUiThread(() -> {
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                            });

                            SharedPreferences.Editor editor = sessionPrefs.edit();
                            editor.putString(Session.LOGIN_KEY, login);
                            editor.putString(Session.PASSWORD_KEY, password);
                            editor.putString(Session.EMAIL_KEY, user.getEmail());
                            editor.apply();

                        } else {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.wrong), Toast.LENGTH_SHORT).show());
                        }

                        Log.println(Log.ASSERT, "user", String.valueOf(user));
                        Log.println(Log.ASSERT, "all", String.valueOf(userDao.getAll()));
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            progressBar.setVisibility(View.GONE);
        });

        signUp.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(intent);
        });

        recover.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, RecoverActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (log != null && pass != null) {
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        }
    }
}