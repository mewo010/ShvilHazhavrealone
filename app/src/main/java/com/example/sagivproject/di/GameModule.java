package com.example.sagivproject.di;

import com.example.sagivproject.services.impl.GameServiceImpl;
import com.example.sagivproject.services.IGameService;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public abstract class GameModule {

    @Binds
    public abstract IGameService bindGameService(GameServiceImpl gameService);
}
