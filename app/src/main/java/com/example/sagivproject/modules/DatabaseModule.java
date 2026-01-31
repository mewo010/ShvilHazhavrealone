package com.example.sagivproject.modules;

import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.services.IDatabaseService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public IDatabaseService provideDatabaseService() {
        return new DatabaseService();
    }
}
