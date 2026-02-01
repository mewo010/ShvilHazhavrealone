package com.example.sagivproject.di;

import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.services.IDatabaseService;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DatabaseModule {

    @Binds
    @Singleton
    public abstract IDatabaseService bindDatabaseService(DatabaseService databaseService);
}