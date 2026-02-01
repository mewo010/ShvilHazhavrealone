package com.example.sagivproject.di;

import com.example.sagivproject.services.AuthService;
import com.example.sagivproject.services.IAuthService;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class AuthModule {

    @Binds
    @Singleton
    public abstract IAuthService bindAuthService(AuthService authService);

}