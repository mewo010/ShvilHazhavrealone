package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;

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

        User user = sharedPreferencesUtil.getUser();

        Button btnToUserTable = findViewById(R.id.btn_admin_to_UsersTablePage);
        Button btnToMedicationsTable = findViewById(R.id.btn_admin_to_MedicineImagesTablePage);
        Button btnToMemoryGameLogsTable = findViewById(R.id.btn_admin_to_MemoryGameLogsTablePage);
        Button btnToForumCategories = findViewById(R.id.btn_admin_to_ForumCategories);
        Button btnToDetailsAboutUser = findViewById(R.id.btn_admin_to_DetailsAboutUser);
        Button btnToSettings = findViewById(R.id.btn_admin_to_Settings);
        Button btnLogout = findViewById(R.id.btn_admin_to_exit);
        TextView txtAdminTitle = findViewById(R.id.txt_admin_title);

        btnToUserTable.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, UsersTableActivity.class)));
        btnToMedicationsTable.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, MedicationImagesTableActivity.class)));
        btnToMemoryGameLogsTable.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, MemoryGameLogsTableActivity.class)));
        btnToForumCategories.setOnClickListener(v -> startActivity(new Intent(AdminPageActivity.this, AdminForumCategoriesActivity.class)));
        btnToDetailsAboutUser.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, DetailsAboutUserActivity.class)));
        btnToSettings.setOnClickListener(view -> startActivity(new Intent(AdminPageActivity.this, SettingsActivity.class)));
        btnLogout.setOnClickListener(view -> logout());

        if (user != null) {
            txtAdminTitle.setText(String.format("שלום %s", user.getFullName()));
        }
    }
}