package com.example.sagivproject.di;

import com.example.sagivproject.services.IUserService;
import com.example.sagivproject.services.impl.UserService;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class UserModule {
    @Binds
    public abstract IUserService bindUserService(UserService userService);
}
