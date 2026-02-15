package com.example.sagivproject.di;

import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.services.IDatabaseService;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * A Hilt module for providing core database-related dependencies.
 * <p>
 * This module is responsible for providing the root {@link DatabaseReference} for Firebase
 * and binding the main {@link IDatabaseService} façade to its implementation.
 * </p>
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class DatabaseModule {

    /**
     * Provides a singleton instance of the Firebase Realtime Database root reference.
     *
     * @return The root {@link DatabaseReference}.
     */
    @Provides
    @Singleton
    public static DatabaseReference provideDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }

    /**
     * Binds the {@link IDatabaseService} interface to its concrete implementation, {@link DatabaseService}.
     *
     * @param databaseService The implementation of the database service.
     * @return The bound {@link IDatabaseService} interface.
     */
    @Binds
    @Singleton
    public abstract IDatabaseService bindDatabaseService(DatabaseService databaseService);
}
