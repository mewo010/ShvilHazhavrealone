package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.utils.SharedPreferencesUtil;
import com.example.sagivproject.workers.BirthdayWorker;

import java.util.concurrent.TimeUnit;

public class AdminPageActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.adminPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupBirthdayNotification();

        Button btnToUserTable = findViewById(R.id.btn_admin_to_UsersTablePage);
        Button btnToMedicationsTable = findViewById(R.id.btn_admin_to_MedicineImagesTablePage);
        Button btnToMemoryGameLogsTable = findViewById(R.id.btn_admin_to_MemoryGameLogsTablePage);
        Button btnToForum = findViewById(R.id.btn_admin_to_AdminForum);
        Button btnToDetailsAboutUser = findViewById(R.id.btn_admin_to_DetailsAboutUser);
        Button btnLogout = findViewById(R.id.btn_admin_to_exit);
        TextView txtAdminTitle = findViewById(R.id.txt_admin_title);

        btnToUserTable.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, UsersTableActivity.class)));
        btnToMedicationsTable.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, MedicationImagesTableActivity.class)));
        btnToMemoryGameLogsTable.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, MemoryGameLogsTableActivity.class)));
        btnToForum.setOnClickListener(v -> startActivity(new Intent(AdminPageActivity.this, AdminForumActivity.class)));
        btnToDetailsAboutUser.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, DetailsAboutUserActivity.class)));
        btnLogout.setOnClickListener(view -> logout());

        User user = SharedPreferencesUtil.getUser(this);
        if (user.getFullName() == null || user.getFullName().trim().isEmpty()) {
            txtAdminTitle.setText("שלום מנהל יקר");
        } else {
            txtAdminTitle.setText("שלום " + user.getFullName());
        }
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