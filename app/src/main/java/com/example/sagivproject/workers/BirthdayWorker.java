package com.example.sagivproject.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import com.example.sagivproject.bases.BaseWorkerActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.services.NotificationService;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.Calendar;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class BirthdayWorker extends BaseWorkerActivity {
    public BirthdayWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        if (!SharedPreferencesUtil.isUserLoggedIn(context)) return Result.success();

        String userId = SharedPreferencesUtil.getUserId(context);
        final CountDownLatch latch = new CountDownLatch(1);

        databaseService.getUser(userId, new DatabaseService.DatabaseCallback<User>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    checkAndNotifyBirthday(context, user);
                }
                latch.countDown();
            }

            @Override
            public void onFailed(Exception e) {
                latch.countDown();
            }
        });

        try {
            latch.await(1, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            return Result.retry();
        }

        return Result.success();
    }

    private void checkAndNotifyBirthday(Context context, User user) {
        if (user.getBirthDateMillis() <= 0) return;

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTimeInMillis(user.getBirthDateMillis());

        if (today.get(Calendar.DAY_OF_MONTH) == birthDate.get(Calendar.DAY_OF_MONTH) && today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH)) {
            NotificationService notificationService = new NotificationService(context);
            notificationService.show(
                    "מזל טוב!",
                    "יום הולדת שמח, " + user.getFirstName() + "! מאחלים לך בריאות ואושר."
            );
        }
    }
}