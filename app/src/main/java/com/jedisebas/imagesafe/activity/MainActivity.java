package com.jedisebas.imagesafe.activity;

import androidx.appcompat.app.AppCompatActivity;

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

import com.jedisebas.imagesafe.R;
import com.jedisebas.imagesafe.util.SafeDatabase;
import com.jedisebas.imagesafe.dao.UserDao;
import com.jedisebas.imagesafe.entity.User;
import com.jedisebas.imagesafe.util.ThemeUtil;

public class MainActivity extends AppCompatActivity {

    private String log;
    private String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeUtil(this);
        setContentView(R.layout.activity_main);

        final EditText loginEt = findViewById(R.id.loginEt);
        final EditText passwordEt = findViewById(R.id.passwordEt);
        final Button signInBtn = findViewById(R.id.signInBtn);
        final ProgressBar progressBar = findViewById(R.id.loading);
        final TextView signUp = findViewById(R.id.signUpTv);
        final TextView recover = findViewById(R.id.recoverTv);
        final SharedPreferences sessionPrefs = getSharedPreferences(getString(R.string.SHARED_PREFS), Context.MODE_PRIVATE);
        log = sessionPrefs.getString(getString(R.string.login_key), null);
        pass = sessionPrefs.getString(getString(R.string.password_key), null);

        signUp.getPaint().setUnderlineText(true);

        final SafeDatabase db = SafeDatabase.getInstance(this);
        final UserDao userDao = db.userDao();

        signInBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            final String login = loginEt.getText().toString().trim();
            final String password = passwordEt.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, getText(R.string.not_all), Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, getText(R.string.wait_sign_in), Toast.LENGTH_SHORT).show();
                try {

                    new Thread(() -> {
                        final User user = userDao.findByLogin(login);

                        if (user != null && user.getPassword().equals(password)) {
                            runOnUiThread(() -> startActivity(new Intent(this, HomeActivity.class)));

                            final SharedPreferences.Editor editor = sessionPrefs.edit();
                            editor.putString(getString(R.string.login_key), login);
                            editor.putString(getString(R.string.password_key), password);
                            editor.putString(getString(R.string.email_key), user.getEmail());
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

        signUp.setOnClickListener(view -> startActivity(new Intent(this, SignUpActivity.class)));

        recover.setOnClickListener(view -> startActivity(new Intent(this, RecoverActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (log != null && pass != null) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }
}