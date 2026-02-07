package com.example.sagivproject.di;

import com.example.sagivproject.services.impl.AuthServiceImpl;
import com.example.sagivproject.services.impl.ForumServiceImpl;
import com.example.sagivproject.services.impl.MedicationServiceImpl;
import com.example.sagivproject.services.impl.StatsServiceImpl;
import com.example.sagivproject.services.IAuthService;
import com.example.sagivproject.services.IForumService;
import com.example.sagivproject.services.IMedicationService;
import com.example.sagivproject.services.IStatsService;

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
    public abstract IAuthService bindAuthService(AuthServiceImpl authService);

    @Binds
    @Singleton
    public abstract IMedicationService bindMedicationService(MedicationServiceImpl medicationService);

    @Binds
    @Singleton
    public abstract IStatsService bindStatsService(StatsServiceImpl statsService);

    @Binds
    @Singleton
    public abstract IForumService bindForumService(ForumServiceImpl forumService);
}
