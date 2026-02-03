package com.example.sagivproject.di;

import com.example.sagivproject.services.ForumService;
import com.example.sagivproject.services.GameService;
import com.example.sagivproject.services.ImageService;
import com.example.sagivproject.services.MedicationService;
import com.example.sagivproject.services.StatsService;
import com.example.sagivproject.services.UserService;
import com.example.sagivproject.services.interfaces.IForumService;
import com.example.sagivproject.services.interfaces.IGameService;
import com.example.sagivproject.services.interfaces.IImageService;
import com.example.sagivproject.services.interfaces.IMedicationService;
import com.example.sagivproject.services.interfaces.IStatsService;
import com.example.sagivproject.services.interfaces.IUserService;
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
    @Singleton
    public abstract IUserService bindUserService(UserService userService);

    @Binds
    @Singleton
    public abstract IMedicationService bindMedicationService(MedicationService medicationService);

    @Binds
    @Singleton
    public abstract IForumService bindForumService(ForumService forumService);

    @Binds
    @Singleton
    public abstract IGameService bindGameService(GameService gameService);

    @Binds
    @Singleton
    public abstract IImageService bindImageService(ImageService imageService);

    @Binds
    @Singleton
    public abstract IStatsService bindStatsService(StatsService statsService);
}
