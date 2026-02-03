package com.example.sagivproject.di;

import com.example.sagivproject.services.DatabaseService;
import com.example.sagivproject.services.ForumService;
import com.example.sagivproject.services.GameService;
import com.example.sagivproject.services.interfaces.IDatabaseService;
import com.example.sagivproject.services.interfaces.IForumService;
import com.example.sagivproject.services.interfaces.IGameService;
import com.example.sagivproject.services.interfaces.IImageService;
import com.example.sagivproject.services.interfaces.IMedicationService;
import com.example.sagivproject.services.interfaces.IStatsService;
import com.example.sagivproject.services.interfaces.IUserService;
import com.example.sagivproject.services.ImageService;
import com.example.sagivproject.services.MedicationService;
import com.example.sagivproject.services.StatsService;
import com.example.sagivproject.services.UserService;

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
