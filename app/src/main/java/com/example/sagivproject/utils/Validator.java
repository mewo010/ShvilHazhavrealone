package com.example.sagivproject.utils;

import android.util.Patterns;

import androidx.annotation.Nullable;

import java.util.Calendar;

public final class Validator {
    private static final int MIN_AGE = 12;

    private Validator() {} // מונע יצירה

    public static boolean isNameValid(@Nullable String name) {
        return name != null && name.trim().length() >= 3;
    }

    public static boolean isEmailValid(@Nullable String email) {
        return email != null && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isPasswordValid(@Nullable String password) {
        return password != null && password.length() >= 6;
    }

    public static boolean isAgeValid(long birthDateMillis) {
        Calendar birth = Calendar.getInstance();
        birth.setTimeInMillis(birthDateMillis);

        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }

        return age >= MIN_AGE;
    }
}