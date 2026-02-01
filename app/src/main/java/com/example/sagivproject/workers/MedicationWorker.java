package com.example.sagivproject.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sagivproject.models.Medication;
import com.example.sagivproject.services.IDatabaseService;
import com.example.sagivproject.services.NotificationService;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class MedicationWorker extends Worker {
    protected final IDatabaseService databaseService;
    protected final NotificationService notificationService;

    @AssistedInject
    public MedicationWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            IDatabaseService databaseService,
            NotificationService notificationService
    ) {
        super(context, workerParams);
        this.databaseService = databaseService;
        this.notificationService = notificationService;
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();

        if (!SharedPreferencesUtil.isUserLoggedIn(context)) {
            return Result.success();
        }

        String userId = SharedPreferencesUtil.getUserId(context);
        final CountDownLatch latch = new CountDownLatch(1);

        databaseService.getUserMedicationList(Objects.requireNonNull(userId), new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(List<Medication> medications) {
                processMedications(context, userId, medications);
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                latch.countDown();
            }
        });

        boolean completed;

        try {
            completed = latch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            return Result.retry();
        }

        if (!completed) {
            return Result.retry();
        }

        return Result.success();
    }

    private void processMedications(Context context, String userId, List<Medication> medications) {
        if (medications == null || medications.isEmpty()) return;

        int expiredCount = 0;
        int remainingCount = 0;

        Date today = new Date();

        for (Medication med : medications) {
            if (med.getDate() != null) {
                Calendar expiryLimit = Calendar.getInstance();
                expiryLimit.setTime(med.getDate());
                expiryLimit.add(Calendar.DAY_OF_YEAR, 1);

                if (today.after(expiryLimit.getTime())) {
                    // תרופה פגת תוקף
                    expiredCount++;
                    databaseService.deleteMedication(userId, med.getId(), null);
                } else {
                    // תרופה תקינה שנותרה ברשימה
                    remainingCount++;
                }
            } else {
                // תרופה ללא תאריך נחשבת כתרופה שנותרה
                remainingCount++;
            }
        }


        //התראה 1: רק אם יש תרופות שנמחקו
        if (expiredCount > 0) {
            notificationService.show(
                    "עדכון רשימת תרופות",
                    "מחקנו " + expiredCount + " תרופות שפג תוקפן מהרשימה שלך."
            );
        }

        //התראה 2: רק אם נשארו תרופות לנטילה (אחרי המחיקה)
        if (remainingCount > 0) {
            notificationService.show(
                    "תזכורת יומית",
                    "יש לך " + remainingCount + " תרופות ברשימה שממתינות לנטילה."
            );
        }
    }
}