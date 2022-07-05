package com.jedisebas.imagesafe;

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
            String login = loginEt.getText().toString().trim();
            String email = emailEt.getText().toString().trim();

            SafeDatabase db = SafeDatabase.getInstance(this);
            UserDao userDao = db.userDao();

            new Thread(() -> {
                User user = userDao.findByLogin(login);
                if (user.getEmail() != null && user.getEmail().equals(email)) {

                    Intent intent = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                            .setSmallIcon(R.drawable.ic_launcher_background)
                            .setContentTitle(getString(R.string.notification_title))
                            .setContentText(getString(R.string.notification_message) + " " + user.getPassword())
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(getString(R.string.notification_message) + " " + user.getPassword()))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
                    notificationManager.notify(1, builder.build());

                } else {
                    runOnUiThread(() -> Toast.makeText(this, getString(R.string.wrong), Toast.LENGTH_SHORT).show());
                }
            }).start();
        });

        setTitle(getString(R.string.recovery_title));
    }

    private void createNotificationChannel() {
        CharSequence name = "R.string.channel_name";
        String description = "R.string.channel_description";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }
}