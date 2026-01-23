package com.example.sagivproject.screens;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.models.User;
import com.example.sagivproject.services.AuthService;
import com.example.sagivproject.utils.Validator;

public class LoginActivity extends BaseActivity {
    private EditText editTextEmail, editTextPassword;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authService = new AuthService(this);

        Button btnToLanding = findViewById(R.id.btn_login_to_landing);
        Button btnToContact = findViewById(R.id.btn_login_to_contact);
        Button btnToRegister = findViewById(R.id.btn_login_to_register);
        Button btnLogin = findViewById(R.id.btnLogin);
        ImageButton btnToSettings = findViewById(R.id.btn_login_to_settings);

        editTextEmail = findViewById(R.id.edt_login_email);
        editTextPassword = findViewById(R.id.edt_login_password);

        btnToLanding.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, LandingActivity.class)));
        btnToContact.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, ContactActivity.class)));
        btnToRegister.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, RegisterActivity.class)));
        btnLogin.setOnClickListener(view -> tryLogin());
        btnToSettings.setOnClickListener(view -> startActivity(new Intent(this, SettingsActivity.class)));

        String lastEmail = getIntent().getStringExtra("userEmail");
        if (lastEmail != null && !lastEmail.isEmpty()) {
            editTextEmail.setText(lastEmail);
        }
    }

    private void tryLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!validateInput(email, password)) {
            return;
        }

        authService.login(email, password, new AuthService.LoginCallback() {
            @Override
            public void onSuccess(User user) {
                Intent intent;

                if (user.getIsAdmin()) {
                    Toast.makeText(LoginActivity.this, "התחברת למשתמש מנהל בהצלחה!", Toast.LENGTH_SHORT).show();
                    intent = new Intent(LoginActivity.this, AdminPageActivity.class);
                } else {
                    Toast.makeText(LoginActivity.this, "התחברת בהצלחה!", Toast.LENGTH_SHORT).show();
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                }

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private boolean validateInput(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "נא למלא אימייל וסיסמה", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Validator.isEmailValid(email)) {
            editTextEmail.requestFocus();
            Toast.makeText(this, "כתובת האימייל אינה תקינה", Toast.LENGTH_LONG).show();
            return false;
        }

        if (Validator.isPasswordValid(password)) {
            editTextPassword.requestFocus();
            Toast.makeText(this, "הסיסמה קצרה מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }
}