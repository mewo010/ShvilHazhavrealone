package com.example.sagivproject.utils;

import android.content.Context;

import com.example.sagivproject.services.AuthService;
import com.example.sagivproject.services.IAuthService;

public class Injector {
    public static IAuthService provideAuthService(Context context) {
        return new AuthService(context);
    }
}