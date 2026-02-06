package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.workers.BirthdayWorker;
import com.example.sagivproject.workers.MedicationWorker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class MainActivity extends BaseActivity implements BaseActivity.RequiresPermissions {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ViewGroup topMenuContainer = findViewById(R.id.topMenuContainer);
        setupTopMenu(topMenuContainer);

        User user = sharedPreferencesUtil.getUser();
        setupDailyNotifications();
        setupBirthdayNotification();

        Button btnToMedicationList = findViewById(R.id.btn_main_to_MedicationList);
        Button btnToForum = findViewById(R.id.btn_main_to_forum);
        Button btnToAi = findViewById(R.id.btn_main_to_Ai);
        Button btnToGameHomeScreen = findViewById(R.id.btn_main_to_GameHomeScreen);
        Button btnToMathProblems = findViewById(R.id.btn_main_to_MathProblems);
        TextView txtHomePageTitle = findViewById(R.id.txt_main_Title);

        btnToMedicationList.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MedicationListActivity.class)));
        btnToForum.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ForumActivity.class)));
        btnToAi.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AiActivity.class)));
        btnToGameHomeScreen.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, GameHomeScreenActivity.class)));
        btnToMathProblems.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MathProblemsActivity.class)));

        assert user != null;
        txtHomePageTitle.setText(String.format("שלום %s", user.getFullName()));

        //להעלאת תמונות - למחוק בסוף הפרויקט!
        //uploadAllImages();
    }

    //התראות לגבי התרופות
    private void setupDailyNotifications() {
        Calendar currentDate = Calendar.getInstance();
        Calendar dueDate = Calendar.getInstance();

        dueDate.set(Calendar.HOUR_OF_DAY, 8);
        dueDate.set(Calendar.MINUTE, 0);
        dueDate.set(Calendar.SECOND, 0);
        dueDate.set(Calendar.MILLISECOND, 0);

        if (dueDate.before(currentDate)) {
            dueDate.add(Calendar.DAY_OF_YEAR, 1);
        }

        long timeDiff = dueDate.getTimeInMillis() - currentDate.getTimeInMillis();

        PeriodicWorkRequest notificationRequest =
                new PeriodicWorkRequest.Builder(
                        MedicationWorker.class,
                        24, TimeUnit.HOURS)
                        .setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .addTag("MedicationWorkTag")
                        .setConstraints(new Constraints.Builder()
                                .setRequiredNetworkType(NetworkType.CONNECTED)
                                .setRequiresBatteryNotLow(true)
                                .build())
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "MedicationDailyWork",
                ExistingPeriodicWorkPolicy.KEEP,
                notificationRequest
        );
    }

    //התראה על יום הולדת
    private void setupBirthdayNotification() {
        PeriodicWorkRequest birthdayRequest =
                new PeriodicWorkRequest.Builder(BirthdayWorker.class, 24, TimeUnit.HOURS)
                        .addTag("BirthdayWorkTag")
                        .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "BirthdayDailyWork",
                ExistingPeriodicWorkPolicy.KEEP,
                birthdayRequest
        );
    }
}