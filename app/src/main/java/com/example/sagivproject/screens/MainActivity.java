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

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;

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

        Button btnToMedicationList = findViewById(R.id.btn_main_to_MedicationList);
        Button btnToForum = findViewById(R.id.btn_main_to_forum);
        Button btnToAi = findViewById(R.id.btn_main_to_Ai);
        Button btnToGameHomeScreen = findViewById(R.id.btn_main_to_GameHomeScreen);
        Button btnToMathProblems = findViewById(R.id.btn_main_to_MathProblems);
        TextView txtHomePageTitle = findViewById(R.id.txt_main_Title);

        btnToMedicationList.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MedicationListActivity.class)));
        btnToForum.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, ForumCategoriesActivity.class)));
        btnToAi.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AiActivity.class)));
        btnToGameHomeScreen.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, GameHomeScreenActivity.class)));
        btnToMathProblems.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MathProblemsActivity.class)));

        if (user != null) {
            txtHomePageTitle.setText(String.format("שלום %s", user.getFullName()));
        }
    }
}
/*
https://1drv.ms/w/c/b29d00b16b90197e/IQB5GYxFy5jcSqn2ijhKrBt5AZ-i9bVXCN5uMkeosIqI23I?e=O2m3N9
 */