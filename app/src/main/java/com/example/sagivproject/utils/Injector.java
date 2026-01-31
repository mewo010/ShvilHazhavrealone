package com.example.sagivproject.utils;

import android.content.Context;

import com.example.sagivproject.services.AuthService;
import com.example.sagivproject.services.DatabaseServiceAdapter;
import com.example.sagivproject.services.IAuthService;
import com.example.sagivproject.services.IDatabaseService;

public class Injector {

    public static IDatabaseService provideDatabaseService() {
        return new DatabaseServiceAdapter();
    }

    public static IAuthService provideAuthService(Context context) {
        return new AuthService(context, provideDatabaseService());
    }
}
