package com.example.sagivproject.utils;

import android.util.Patterns;

import androidx.annotation.Nullable;

import java.util.Calendar;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class Validator {
    private static final int MIN_AGE = 12;

    @Inject
    public Validator() {
    }

    public boolean isNameNotValid(@Nullable String name) {
        return name == null || name.trim().length() < 3;
    }

    public boolean isEmailNotValid(@Nullable String email) {
        return email == null || !Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isPasswordNotValid(@Nullable String password) {
        return password == null || password.length() < 6;
    }

    public boolean isAgeNotValid(long birthDateMillis) {
        Calendar birth = Calendar.getInstance();
        birth.setTimeInMillis(birthDateMillis);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age < MIN_AGE;
    }
}