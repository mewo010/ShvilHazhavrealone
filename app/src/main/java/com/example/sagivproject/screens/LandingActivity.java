package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;

public class LandingActivity extends BaseActivity implements BaseActivity.RequiresPermissions {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_landing);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.landing_page), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button btnToContact = findViewById(R.id.btn_landingBody_to_contact);
        Button btnToLogin = findViewById(R.id.btn_landingBody_to_login);
        Button btnToRegister = findViewById(R.id.btn_landingBody_to_register);
        Button btnNavToContact = findViewById(R.id.btn_landing_to_contact);
        Button btnNavToLogin = findViewById(R.id.btn_landing_to_login);
        Button btnNavToRegister = findViewById(R.id.btn_landing_to_register);
        ImageButton btnToSettings = findViewById(R.id.btn_landing_to_settings);

        btnToContact.setOnClickListener(view -> startActivity(new Intent(LandingActivity.this, ContactActivity.class)));
        btnNavToContact.setOnClickListener(view -> startActivity(new Intent(LandingActivity.this, ContactActivity.class)));
        btnToLogin.setOnClickListener(view -> startActivity(new Intent(LandingActivity.this, LoginActivity.class)));
        btnNavToLogin.setOnClickListener(view -> startActivity(new Intent(LandingActivity.this, LoginActivity.class)));
        btnToRegister.setOnClickListener(view -> startActivity(new Intent(LandingActivity.this, RegisterActivity.class)));
        btnNavToRegister.setOnClickListener(view -> startActivity(new Intent(LandingActivity.this, RegisterActivity.class)));
        btnToSettings.setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));
    }
}