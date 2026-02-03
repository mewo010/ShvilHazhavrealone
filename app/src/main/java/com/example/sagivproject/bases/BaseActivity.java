package com.example.sagivproject.bases;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.screens.ContactActivity;
import com.example.sagivproject.screens.DetailsAboutUserActivity;
import com.example.sagivproject.screens.LandingActivity;
import com.example.sagivproject.screens.LoginActivity;
import com.example.sagivproject.screens.MainActivity;
import com.example.sagivproject.screens.RegisterActivity;
import com.example.sagivproject.screens.SettingsActivity;
import com.example.sagivproject.screens.dialogs.LogoutDialog;
import com.example.sagivproject.services.interfaces.IAuthService;
import com.example.sagivproject.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public abstract class BaseActivity extends AppCompatActivity {
    @Inject
    protected IAuthService authService;
    @Inject
    protected SharedPreferencesUtil sharedPreferencesUtil;

    protected IAuthService getAuthService() {
        return authService;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (this instanceof RequiresPermissions) {
            requestPermissions();
        }
    }

    protected void setupTopMenu(ViewGroup menuContainer) {
        boolean isUserLoggedIn = sharedPreferencesUtil.isUserLoggedIn();

        if (isUserLoggedIn) {
            if (Objects.requireNonNull(sharedPreferencesUtil.getUser()).isAdmin()) {
                @LayoutRes int menuLayout = R.layout.top_menu_admin;
                getLayoutInflater().inflate(menuLayout, menuContainer, true);

                Button btnAdmin = findViewById(R.id.btn_menu_admin_back);
                btnAdmin.setOnClickListener(v -> finish());
            } else {
                @LayoutRes int menuLayout = R.layout.top_menu_logged_in;
                getLayoutInflater().inflate(menuLayout, menuContainer, true);

                Button btnMain = findViewById(R.id.btn_menu_main);
                Button btnContact = findViewById(R.id.btn_menu_contact);
                Button btnDetailsAboutUser = findViewById(R.id.btn_menu_details);
                ImageButton btnSettings = findViewById(R.id.btn_menu_settings);
                Button btnLogout = findViewById(R.id.btn_menu_logout);

                btnMain.setOnClickListener(v -> navigateIfNotCurrent(MainActivity.class));
                btnContact.setOnClickListener(v -> navigateIfNotCurrent(ContactActivity.class));
                btnDetailsAboutUser.setOnClickListener(v -> navigateIfNotCurrent(DetailsAboutUserActivity.class));
                btnSettings.setOnClickListener(v -> navigateIfNotCurrent(SettingsActivity.class));
                btnLogout.setOnClickListener(v -> logout());
            }
        } else {
            @LayoutRes int menuLayout = R.layout.top_menu_logged_out;
            getLayoutInflater().inflate(menuLayout, menuContainer, true);

            Button btnLanding = findViewById(R.id.btn_menu_main);
            Button btnContact = findViewById(R.id.btn_menu_contact);
            Button btnLogin = findViewById(R.id.btn_menu_login);
            Button btnRegister = findViewById(R.id.btn_menu_register);
            ImageButton btnSettings = findViewById(R.id.btn_menu_settings);

            btnLanding.setOnClickListener(v -> navigateIfNotCurrent(LandingActivity.class));
            btnContact.setOnClickListener(v -> navigateIfNotCurrent(ContactActivity.class));
            btnLogin.setOnClickListener(v -> navigateIfNotCurrent(LoginActivity.class));
            btnRegister.setOnClickListener(v -> navigateIfNotCurrent(RegisterActivity.class));
            btnSettings.setOnClickListener(v -> navigateIfNotCurrent(SettingsActivity.class));
        }
    }

    protected void navigateIfNotCurrent(Class<?> targetActivity) {
        if (this.getClass().equals(targetActivity)) {
            return;
        }
        startActivity(new Intent(this, targetActivity));
    }

    protected void logout() {
        new LogoutDialog(this, () -> {
            String email = getAuthService().logout();
            Toast.makeText(this, "התנתקת בהצלחה", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("userEmail", email);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }).show();
    }

    protected void requestPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.POST_NOTIFICATIONS);

        ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), 1001);
    }

    public interface RequiresPermissions {
    }
}
