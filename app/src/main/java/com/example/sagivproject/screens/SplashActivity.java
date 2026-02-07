package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.DatabaseCallback;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SplashActivity extends BaseActivity {
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splashPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Thread splashThread = new Thread(() -> {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ignored) {
            } finally {
                if (sharedPreferencesUtil.isUserLoggedIn()) {
                    User current = sharedPreferencesUtil.getUser();
                    if (current != null) {
                        databaseService.users().getUser(current.getUid(), new DatabaseCallback<>() {
                            @Override
                            public void onCompleted(User user) {
                                if (user != null) {
                                    sharedPreferencesUtil.saveUser(user);
                                    if (user.isAdmin()) {
                                        intent = new Intent(SplashActivity.this, AdminPageActivity.class);
                                    } else {
                                        intent = new Intent(SplashActivity.this, MainActivity.class);
                                    }
                                } else {
                                    sharedPreferencesUtil.signOutUser();
                                    intent = new Intent(SplashActivity.this, LandingActivity.class);
                                }
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onFailed(Exception e) {
                                sharedPreferencesUtil.signOutUser();
                                intent = new Intent(SplashActivity.this, LandingActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        });
                    }
                } else {
                    intent = new Intent(SplashActivity.this, LandingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
            }
        });
        splashThread.start();
    }
}
