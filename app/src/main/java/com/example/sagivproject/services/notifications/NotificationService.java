package com.example.sagivproject.services.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.screens.MedicationListActivity;
import com.example.sagivproject.screens.SplashActivity;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class NotificationService {
    public static final String MEDICATIONS_CHANNEL_ID = "medication_notifications";
    public static final String BIRTHDAYS_CHANNEL_ID = "birthday_notifications";
    private static final String MEDICATIONS_CHANNEL_NAME = "תזכורות תרופות";
    private static final String BIRTHDAYS_CHANNEL_NAME = "תזכורות יום הולדת";
    private final Context context;
    private final NotificationManager manager;

    @Inject
    public NotificationService(@ApplicationContext Context context) {
        this.context = context.getApplicationContext();
        this.manager = (NotificationManager)
                this.context.getSystemService(Context.NOTIFICATION_SERVICE);

        createMedicationChannelIfNeeded();
        createBirthdayChannelIfNeeded();
    }

    private void createMedicationChannelIfNeeded() {
        NotificationChannel channel = new NotificationChannel(
                MEDICATIONS_CHANNEL_ID,
                MEDICATIONS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        manager.createNotificationChannel(channel);
    }

    private void createBirthdayChannelIfNeeded() {
        NotificationChannel channel = new NotificationChannel(
                BIRTHDAYS_CHANNEL_ID,
                BIRTHDAYS_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        manager.createNotificationChannel(channel);
    }

    public void showMedicationNotification(String medicationName) {
        String title = "תזכורת תרופה";
        String message = "הגיע הזמן לקחת את התרופה: " + medicationName;

        Intent intent = new Intent(context, MedicationListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        show(MEDICATIONS_CHANNEL_ID, title, message, pendingIntent);
    }

    public void showBirthdayNotification(String firstName) {
        String title = "מזל טוב!";
        String message = "יום הולדת שמח, " + firstName + "! מאחלים לך בריאות ואושר.";

        Intent intent = new Intent(context, SplashActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        show(BIRTHDAYS_CHANNEL_ID, title, message, pendingIntent);
    }

    private void show(String channelId, String title, String message, PendingIntent pendingIntent) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}