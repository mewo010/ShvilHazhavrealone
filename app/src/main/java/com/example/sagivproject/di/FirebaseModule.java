package com.example.sagivproject.di;

import com.google.firebase.database.FirebaseDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * A Hilt module for providing the core Firebase Database dependency.
 */
@Module
@InstallIn(SingletonComponent.class)
public class FirebaseModule {

    /**
     * Provides a singleton instance of the {@link FirebaseDatabase}.
     *
     * @return The singleton {@link FirebaseDatabase} instance.
     */
    @Provides
    @Singleton
    public FirebaseDatabase provideFirebaseDatabase() {
        return FirebaseDatabase.getInstance();
    }
}
