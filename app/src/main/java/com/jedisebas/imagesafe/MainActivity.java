package com.jedisebas.imagesafe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText loginEt = findViewById(R.id.loginEt);
        final EditText passwordEt = findViewById(R.id.passwordEt);
        final Button signInBtn = findViewById(R.id.signInBtn);
        final ProgressBar progressBar = findViewById(R.id.loading);

        SafeDatabase db = Room.databaseBuilder(getApplicationContext(), SafeDatabase.class, "safe").build();
        UserDao userDao = db.userDao();

        signInBtn.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String login = loginEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();

            if (login.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Not all data", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Signing in...", Toast.LENGTH_SHORT).show();
                try {
                    User user = userDao.findByLogin(login);
                    if (password.equals(user.getPassword())) {
                        //TODO new intent
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "This user does not exist", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}