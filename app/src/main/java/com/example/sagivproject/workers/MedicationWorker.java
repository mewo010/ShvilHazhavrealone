package com.example.sagivproject.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sagivproject.models.Medication;
import com.example.sagivproject.services.NotificationService;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.Calendar;
import java.util.HashMap;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class MedicationWorker extends Worker {
    protected final NotificationService notificationService;
    protected final SharedPreferencesUtil sharedPreferencesUtil;

    @AssistedInject
    public MedicationWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            NotificationService notificationService,
            SharedPreferencesUtil sharedPreferencesUtil
    ) {
        super(context, workerParams);
        this.notificationService = notificationService;
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!sharedPreferencesUtil.isUserLoggedIn()) return Result.success();

        HashMap<String, Medication> medications = sharedPreferencesUtil.getMedications();
        if (medications == null) {
            return Result.success();
        }

        checkAndNotifyMedications(medications);

        return Result.success();
    }

    private void checkAndNotifyMedications(HashMap<String, Medication> medications) {
        Calendar today = Calendar.getInstance();

        for (Medication medication : medications.values()) {
            if (medication.getDate() != null) {
                Calendar expiryDate = Calendar.getInstance();
                expiryDate.setTime(medication.getDate());

                long daysUntilExpiry = (expiryDate.getTimeInMillis() - today.getTimeInMillis()) / (1000 * 60 * 60 * 24);

                if (daysUntilExpiry == 7 || daysUntilExpiry == 3) {
                    notificationService.show(
                            "תוקף תרופה מתקרב",
                            "תוקף התרופה '" + medication.getName() + "' יפוג בעוד " + daysUntilExpiry + " ימים."
                    );
                }
            }
        }
    }
}