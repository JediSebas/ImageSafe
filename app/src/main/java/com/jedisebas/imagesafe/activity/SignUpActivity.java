package com.jedisebas.imagesafe.activity;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.jedisebas.imagesafe.R;
import com.jedisebas.imagesafe.util.SafeDatabase;
import com.jedisebas.imagesafe.dao.UserDao;
import com.jedisebas.imagesafe.entity.User;

import java.io.File;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        final EditText loginEt = findViewById(R.id.loginEt);
        final EditText passwordEt = findViewById(R.id.passwordEt);
        final EditText repeatEt = findViewById(R.id.repeatEt);
        final Button signUpBtn = findViewById(R.id.signUpBtn);
        final ProgressBar progressBar = findViewById(R.id.loading);

        final SafeDatabase db = SafeDatabase.getInstance(this);
        final UserDao userDao = db.userDao();

        signUpBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            final String login = loginEt.getText().toString().trim();
            final String password = passwordEt.getText().toString().trim();
            final String password2 = repeatEt.getText().toString().trim();
            
            if (login.isEmpty() || password.isEmpty() || password2.isEmpty()) {
                Toast.makeText(this, getString(R.string.not_all), Toast.LENGTH_SHORT).show();
            } else if (password.equals(password2)){
                Toast.makeText(this, getString(R.string.wait_sign_up), Toast.LENGTH_SHORT).show();
                try {

                    new Thread(() -> {
                        final User newUser = new User(login, password);
                        final User user = userDao.findByLogin(login);

                        if (user == null) {
                            userDao.insertAll(newUser);
                            final File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/DCIM/.secret_safe_" + login);
                            if (myDir.mkdir()) {
                                Log.println(Log.ASSERT, "mkdir", "directory created");
                            }
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.success), Toast.LENGTH_SHORT).show());
                        } else {
                            runOnUiThread(() -> Toast.makeText(getApplicationContext(), getString(R.string.taken), Toast.LENGTH_SHORT).show());
                        }

                        Log.println(Log.ASSERT, "user", String.valueOf(newUser));
                        Log.println(Log.ASSERT, "all", String.valueOf(userDao.getAll()));
                    }).start();

                } catch (final Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, getString(R.string.do_not_match), Toast.LENGTH_SHORT).show();
            }
            progressBar.setVisibility(View.GONE);
        });

        setTitle(getString(R.string.button_sign_up));
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