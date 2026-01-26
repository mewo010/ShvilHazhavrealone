package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.utils.SharedPreferencesUtil;

public class ContactActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_contact);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.contactPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //כפתורים למשתמש מחובר
        Button btnToMain = findViewById(R.id.btn_contact_to_main);
        Button btnToContactPage1 = findViewById(R.id.btn_contact_to_contact1);
        Button btnToDetailsAboutUser = findViewById(R.id.btn_contact_to_DetailsAboutUser);
        Button btnToExit = findViewById(R.id.btn_contact_to_exit);
        ImageButton btnToSettings1 = findViewById(R.id.btn_contact_to_settings1);

        //כפתורים למשתמש לא מחובר
        Button btnToLanding = findViewById(R.id.btn_contact_to_landing);
        Button btnToContactPage2 = findViewById(R.id.btn_contact_to_contact2);
        Button btnToLoginPage = findViewById(R.id.btn_contact_to_login);
        Button btnToRegisterPage = findViewById(R.id.btn_contact_to_register);
        ImageButton btnToSettings2 = findViewById(R.id.btn_contact_to_settings2);

        //בדיקה אם המשתמש מחובר
        boolean isLoggedIn = SharedPreferencesUtil.isUserLoggedIn(ContactActivity.this);

        if (isLoggedIn) {
            //הופך את כפתורי המשתמש המחובר ל-VISIBLE
            btnToMain.setVisibility(View.VISIBLE);
            btnToContactPage1.setVisibility(View.VISIBLE);
            btnToDetailsAboutUser.setVisibility(View.VISIBLE);
            btnToExit.setVisibility(View.VISIBLE);
            btnToSettings1.setVisibility(View.VISIBLE);
        } else {
            //הופך את כפתורי המשתמש הלא מחובר ל-VISIBLE
            btnToLanding.setVisibility(View.VISIBLE);
            btnToLoginPage.setVisibility(View.VISIBLE);
            btnToRegisterPage.setVisibility(View.VISIBLE);
            btnToContactPage2.setVisibility(View.VISIBLE);
            btnToSettings2.setVisibility(View.VISIBLE);
        }

        btnToMain.setOnClickListener(view -> startActivity(new Intent(ContactActivity.this, MainActivity.class)));
        btnToDetailsAboutUser.setOnClickListener(view -> startActivity(new Intent(ContactActivity.this, DetailsAboutUserActivity.class)));
        btnToExit.setOnClickListener(view -> logout());
        btnToSettings1.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));

        btnToLanding.setOnClickListener(view -> startActivity(new Intent(ContactActivity.this, LandingActivity.class)));
        btnToLoginPage.setOnClickListener(view -> startActivity(new Intent(ContactActivity.this, LoginActivity.class)));
        btnToRegisterPage.setOnClickListener(view -> startActivity(new Intent(ContactActivity.this, RegisterActivity.class)));
        btnToSettings2.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
    }
}