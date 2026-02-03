package com.example.sagivproject.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sagivproject.models.Medication;
import com.example.sagivproject.services.interfaces.IDatabaseService;
import com.example.sagivproject.services.interfaces.IMedicationService;
import com.example.sagivproject.services.NotificationService;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class MedicationWorker extends Worker {
    protected final IMedicationService medicationService;
    protected final NotificationService notificationService;
    protected final SharedPreferencesUtil sharedPreferencesUtil;

    @AssistedInject
    public MedicationWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            IMedicationService medicationService,
            NotificationService notificationService,
            SharedPreferencesUtil sharedPreferencesUtil
    ) {
        super(context, workerParams);
        this.medicationService = medicationService;
        this.notificationService = notificationService;
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!sharedPreferencesUtil.isUserLoggedIn()) {
            return Result.success();
        }

        String userId = sharedPreferencesUtil.getUserId();
        if (userId == null) {
            return Result.success();
        }

        final CountDownLatch latch = new CountDownLatch(1);

        medicationService.getUserMedicationList(userId, new IDatabaseService.DatabaseCallback<List<Medication>>() {
            @Override
            public void onCompleted(List<Medication> medications) {
                processMedications(userId, medications);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                latch.countDown();
            }
        });

        try {
            // Wait for the database operation to complete
            if (!latch.await(1, TimeUnit.MINUTES)) {
                return Result.retry(); // Timeout
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.retry();
        }

        return Result.success();
    }

    private void processMedications(String userId, List<Medication> medications) {
        if (medications == null || medications.isEmpty()) {
            return;
        }

        int expiredCount = 0;
        int remainingCount = 0;

        Calendar today = Calendar.getInstance();
        // Normalize today to midnight for consistent date comparison
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        for (Medication med : medications) {
            if (med.getDate() != null) {
                Calendar expiryDate = Calendar.getInstance();
                expiryDate.setTime(med.getDate());
                // Normalize expiryDate to midnight
                expiryDate.set(Calendar.HOUR_OF_DAY, 0);
                expiryDate.set(Calendar.MINUTE, 0);
                expiryDate.set(Calendar.SECOND, 0);
                expiryDate.set(Calendar.MILLISECOND, 0);

                // A medication expires the day *after* its printed expiry date.
                // So, if today is after the expiry date, it's expired.
                if (today.after(expiryDate)) {
                    expiredCount++;
                    medicationService.deleteMedication(userId, med.getId(), null);
                } else {
                    remainingCount++;
                    long diffMillis = expiryDate.getTimeInMillis() - today.getTimeInMillis();
                    long daysUntilExpiry = TimeUnit.MILLISECONDS.toDays(diffMillis);

                    if (daysUntilExpiry == 7 || daysUntilExpiry == 3) {
                        notificationService.show(
                                "תוקף תרופה מתקרב",
                                "תוקף התרופה '" + med.getName() + "' יפוג בעוד " + daysUntilExpiry + " ימים."
                        );
                    }
                }
            } else {
                // Medication without an expiry date is considered remaining
                remainingCount++;
            }
        }

        // Notification 1: Only if medications were deleted
        if (expiredCount > 0) {
            notificationService.show(
                    "עדכון רשימת תרופות",
                    "מחקנו " + expiredCount + " תרופות שפג תוקפן מהרשימה שלך."
            );
        }

        // Notification 2: Only if there are remaining medications
        if (remainingCount > 0) {
            notificationService.show(
                    "תזכורת יומית",
                    "יש לך " + remainingCount + " תרופות ברשימה שממתינות לנטילה."
            );
        }
    }
}
