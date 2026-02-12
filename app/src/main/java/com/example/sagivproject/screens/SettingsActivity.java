package com.example.sagivproject.screens;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.screens.dialogs.LogoutDialog;
import com.google.android.material.switchmaterial.SwitchMaterial;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        findViewById(R.id.btn_settings_back).setOnClickListener(v -> finish());
        Button btnLogout = findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(v -> logout());

        SwitchMaterial switchDarkMode = findViewById(R.id.switch_dark_mode);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isDarkMode = sharedPreferences.getBoolean("dark_mode", false);
        switchDarkMode.setChecked(isDarkMode);
        updateDarkModeText(switchDarkMode, isDarkMode);

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("dark_mode", isChecked).apply();
            AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
            updateDarkModeText(switchDarkMode, isChecked);
        });

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void updateDarkModeText(SwitchMaterial switchDarkMode, boolean isDarkMode) {
        if (isDarkMode) {
            switchDarkMode.setText(R.string.מצבבהיר);
        } else {
            switchDarkMode.setText(R.string.מצבכהה);
        }
    }

    private void logout() {
        new LogoutDialog(this, () -> {
            String email = databaseService.getAuthService().logout();
            Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("userEmail", email);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).show();
    }
}