package com.example.sagivproject.services.notifications;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.example.sagivproject.models.Medication;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

@Singleton
public class AlarmScheduler {

    private final Context context;
    private final AlarmManager alarmManager;

    @Inject
    public AlarmScheduler(@ApplicationContext Context context) {
        this.context = context;
        this.alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
    }

    public void schedule(Medication medication) {
        if (medication.getReminderHours() == null) {
            return;
        }

        for (String hourStr : medication.getReminderHours()) {
            String[] time = hourStr.split(":");
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            if (calendar.before(Calendar.getInstance())) {
                calendar.add(Calendar.DATE, 1);
            }

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("medication_name", medication.getName());

            int requestCode = (medication.getId() + hourStr).hashCode();

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
        }
    }

    public void cancel(Medication medication) {
        if (medication.getReminderHours() == null) {
            return;
        }

        for (String hourStr : medication.getReminderHours()) {
            Intent intent = new Intent(context, AlarmReceiver.class);
            int requestCode = (medication.getId() + hourStr).hashCode();
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );
            alarmManager.cancel(pendingIntent);
        }
    }
}
