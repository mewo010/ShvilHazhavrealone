package com.example.sagivproject.di;

import com.example.sagivproject.services.AuthService;
import com.example.sagivproject.services.ForumService;
import com.example.sagivproject.services.MedicationService;
import com.example.sagivproject.services.StatsService;
import com.example.sagivproject.services.interfaces.IAuthService;
import com.example.sagivproject.services.interfaces.IForumService;
import com.example.sagivproject.services.interfaces.IMedicationService;
import com.example.sagivproject.services.interfaces.IStatsService;

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

    @Binds
    @Singleton
    public abstract IMedicationService bindMedicationService(MedicationService medicationService);

    @Binds
    @Singleton
    public abstract IStatsService bindStatsService(StatsService statsService);

    @Binds
    @Singleton
    public abstract IForumService bindForumService(ForumService forumService);
}
