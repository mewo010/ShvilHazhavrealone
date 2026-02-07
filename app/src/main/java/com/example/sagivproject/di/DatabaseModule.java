package com.example.sagivproject.di;

import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.services.IDatabaseService;
import com.example.sagivproject.services.IImageService;
import com.example.sagivproject.services.impl.ImageServiceImpl;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class DatabaseModule {
    @Provides
    @Singleton
    public static DatabaseReference provideDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    @Binds
    public abstract IDatabaseService bindDatabaseService(DatabaseService databaseService);

    @Binds
    public abstract IImageService bindImageService(ImageServiceImpl imageService);
}
