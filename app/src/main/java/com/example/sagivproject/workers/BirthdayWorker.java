package com.example.sagivproject.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.sagivproject.models.User;
import com.example.sagivproject.services.DatabaseCallback;
import com.example.sagivproject.services.IDatabaseService;
import com.example.sagivproject.services.NotificationService;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.Calendar;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

@HiltWorker
public class BirthdayWorker extends Worker {
    protected final IDatabaseService databaseService;
    protected final NotificationService notificationService;
    protected final SharedPreferencesUtil sharedPreferencesUtil;

    @AssistedInject
    public BirthdayWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            IDatabaseService databaseService,
            NotificationService notificationService,
            SharedPreferencesUtil sharedPreferencesUtil
    ) {
        super(context, workerParams);
        this.databaseService = databaseService;
        this.notificationService = notificationService;
        this.sharedPreferencesUtil = sharedPreferencesUtil;
    }

    @NonNull
    @Override
    public Result doWork() {
        if (!sharedPreferencesUtil.isUserLoggedIn()) return Result.success();

        String userId = sharedPreferencesUtil.getUserId();
        final CountDownLatch latch = new CountDownLatch(1);

        databaseService.users().getUser(Objects.requireNonNull(userId), new DatabaseCallback<>() {
            @Override
            public void onCompleted(User user) {
                if (user != null) {
                    checkAndNotifyBirthday(user);
                }
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

    private void checkAndNotifyBirthday(User user) {
        if (user.getBirthDateMillis() <= 0) return;

        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTimeInMillis(user.getBirthDateMillis());

        if (today.get(Calendar.DAY_OF_MONTH) == birthDate.get(Calendar.DAY_OF_MONTH) && today.get(Calendar.MONTH) == birthDate.get(Calendar.MONTH)) {
            notificationService.show(
                    "מזל טוב!",
                    "יום הולדת שמח, " + user.getFirstName() + "! מאחלים לך בריאות ואושר."
            );
        }
    }
}