package com.jedisebas.imagesafe;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText loginEt = findViewById(R.id.loginEt);
        final EditText passwordEt = findViewById(R.id.passwordEt);
        final Button signUpBtn = findViewById(R.id.signUpBtn);
        final ProgressBar progressBar = findViewById(R.id.loading);

        SafeDatabase db = SafeDatabase.getInstance(this);
        UserDao userDao = db.userDao();

        signUpBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String login = loginEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getString(R.string.not_all), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.wait_sign_up), Toast.LENGTH_SHORT).show();
                try {

                    new Thread(() -> {
                        User newUser = new User(login, password);
                        User user = userDao.findByLogin(login);

                        if (user == null) {
                            userDao.insertAll(newUser);
                        } else {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.taken), Toast.LENGTH_SHORT).show());
                        }

                        Log.println(Log.ASSERT, "user", String.valueOf(newUser));
                        Log.println(Log.ASSERT, "all", String.valueOf(userDao.getAll()));
                    }).start();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            progressBar.setVisibility(View.GONE);
        });

        setTitle("Sign Up");
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