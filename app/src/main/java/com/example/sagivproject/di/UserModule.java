package com.example.sagivproject.di;

import com.example.sagivproject.services.UserService;
import com.example.sagivproject.services.interfaces.IUserService;

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
