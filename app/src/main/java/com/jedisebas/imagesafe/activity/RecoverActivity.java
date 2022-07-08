package com.jedisebas.imagesafe.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jedisebas.imagesafe.R;
import com.jedisebas.imagesafe.util.SafeDatabase;
import com.jedisebas.imagesafe.dao.UserDao;
import com.jedisebas.imagesafe.entity.User;

public class RecoverActivity extends AppCompatActivity {

    private static final String CHANNEL_ID = "12";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createNotificationChannel();
        setContentView(R.layout.activity_recover);

        final EditText loginEt = findViewById(R.id.recoveryLoginEt);
        final EditText emailEt = findViewById(R.id.recoveryEmailEt);
        final Button recoveryBtn = findViewById(R.id.recoveryBtn);

        recoveryBtn.setOnClickListener(view -> {
            final String login = loginEt.getText().toString().trim();
            final String email = emailEt.getText().toString().trim();

            final SafeDatabase db = SafeDatabase.getInstance(this);
            final UserDao userDao = db.userDao();

            new Thread(() -> {
                final User user = userDao.findByLogin(login);
                if (user.getEmail() != null && user.getEmail().equals(email)) {

                    final Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    final PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                    final NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(getString(R.string.notification_title))
                            .setContentText(getString(R.string.notification_message, user.getPassword()))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(getString(R.string.notification_message, user.getPassword())))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(1, builder.build());

                } else {
                    runOnUiThread(() -> Toast.makeText(this, getString(R.string.wrong_email), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        setTitle(getString(R.string.recovery_title));
    }

    private void createNotificationChannel() {
        final CharSequence name = "R.string.channel_name";
        final String description = "R.string.channel_description";
        final int importance = NotificationManager.IMPORTANCE_DEFAULT;
        final NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        final NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}