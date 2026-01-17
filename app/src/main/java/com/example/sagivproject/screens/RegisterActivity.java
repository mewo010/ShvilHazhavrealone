package com.example.sagivproject.screens;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.sagivproject.R;
import com.example.sagivproject.bases.BaseActivity;
import com.example.sagivproject.services.AuthService;
import com.example.sagivproject.utils.Validator;

import java.util.Calendar;

public class RegisterActivity extends BaseActivity {
    private Button btnToContact, btnToLanding, btnToLogin, btnRegister;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextBirthDate;
    private int birthYear, birthMonth, birthDay;
    private long birthDateMillis = -1;
    private AuthService authService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.registerPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        authService = new AuthService(this);

        btnToContact = findViewById(R.id.btn_register_to_contact);
        btnToLanding = findViewById(R.id.btn_register_to_landing);
        btnToLogin = findViewById(R.id.btn_register_to_login);
        btnRegister = findViewById(R.id.btnRegister);

        editTextFirstName = findViewById(R.id.edt_register_first_name);
        editTextLastName = findViewById(R.id.edt_register_last_name);
        editTextBirthDate = findViewById(R.id.edt_register_birth_date);
        editTextBirthDate.setOnClickListener(v -> openDatePicker());
        editTextEmail = findViewById(R.id.edt_register_email);
        editTextPassword = findViewById(R.id.edt_register_password);

        editTextBirthDate.setFocusable(false);
        editTextBirthDate.setClickable(true);

        btnToContact.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, ContactActivity.class)));
        btnToLanding.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LandingActivity.class)));
        btnToLogin.setOnClickListener(view -> startActivity(new Intent(RegisterActivity.this, LoginActivity.class)));
        btnRegister.setOnClickListener(view -> tryRegister());
    }

    private void tryRegister() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String birthDate = editTextBirthDate.getText().toString();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (!validateInput(firstName, lastName, birthDate, email, password)) {
            return;
        }

        authService.register(firstName, lastName, birthDateMillis, email, password, new AuthService.RegisterCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(RegisterActivity.this, "ההרשמה בוצעה בהצלחה!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }


    private boolean validateInput(String firstName, String lastName, String birthDate, String email, String password) {
        if (firstName.isEmpty() || lastName.isEmpty() || birthDate.isEmpty() || email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!Validator.isNameValid(firstName)) {
            editTextFirstName.requestFocus();
            Toast.makeText(this, "שם פרטי קצר מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Validator.isNameValid(lastName)) {
            editTextLastName.requestFocus();
            Toast.makeText(this, "שם משפחה קצר מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        if (birthDateMillis <= 0) {
            editTextBirthDate.requestFocus();
            Toast.makeText(this, "נא לבחור תאריך לידה", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Validator.isAgeValid(birthDateMillis)) {
            editTextBirthDate.requestFocus();
            Toast.makeText(this, "הגיל המינימלי להרשמה הוא 12", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Validator.isEmailValid(email)) {
            editTextEmail.requestFocus();
            Toast.makeText(this, "כתובת האימייל אינה תקינה", Toast.LENGTH_LONG).show();
            return false;
        }

        if (!Validator.isPasswordValid(password)) {
            editTextPassword.requestFocus();
            Toast.makeText(this, "הסיסמה קצרה מדי", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void openDatePicker() {
        final Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dialog = new DatePickerDialog(
                this,
                R.style.CustomDatePickerDialog,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    birthYear = selectedYear;
                    birthMonth = selectedMonth;
                    birthDay = selectedDay;

                    Calendar birthCal = Calendar.getInstance();
                    birthCal.set(birthYear, birthMonth, birthDay, 0, 0, 0);
                    birthCal.set(Calendar.MILLISECOND, 0);

                    birthDateMillis = birthCal.getTimeInMillis();

                    String date = String.format("%02d/%02d/%04d",
                            birthDay, birthMonth + 1, birthYear);

                    editTextBirthDate.setText(date);
                },
                year, month, day
        );

        dialog.show();
    }
}