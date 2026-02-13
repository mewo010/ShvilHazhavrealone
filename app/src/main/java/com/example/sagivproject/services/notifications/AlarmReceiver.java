package com.example.sagivproject.services.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class AlarmReceiver extends BroadcastReceiver {
    @Inject
    NotificationService notificationService;

    @Override
    public void onReceive(Context context, Intent intent) {
        String medicationName = intent.getStringExtra("medication_name");
        notificationService.showMedicationNotification(medicationName);
    }
}
