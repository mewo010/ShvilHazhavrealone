package com.example.sagivproject.services;

import com.example.sagivproject.services.interfaces.IDatabaseService;
import com.example.sagivproject.services.interfaces.IForumService;
import com.example.sagivproject.services.interfaces.IGameService;
import com.example.sagivproject.services.interfaces.IImageService;
import com.example.sagivproject.services.interfaces.IMedicationService;
import com.example.sagivproject.services.interfaces.IStatsService;
import com.example.sagivproject.services.interfaces.IUserService;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseService implements IDatabaseService {

    private final IUserService userService;
    private final IMedicationService medicationService;
    private final IGameService gameService;
    private final IStatsService statsService;
    private final IForumService forumService;
    private final IImageService imageService;

    @Inject
    public DatabaseService(
            IUserService userService,
            IMedicationService medicationService,
            IGameService gameService,
            IStatsService statsService,
            IForumService forumService,
            IImageService imageService
    ) {
        this.userService = userService;
        this.medicationService = medicationService;
        this.gameService = gameService;
        this.statsService = statsService;
        this.forumService = forumService;
        this.imageService = imageService;
    }

    @Override
    public IUserService users() {
        return userService;
    }

    @Override
    public IMedicationService medications() {
        return medicationService;
    }

    @Override
    public IGameService games() {
        return gameService;
    }

    @Override
    public IStatsService stats() {
        return statsService;
    }

    @Override
    public IForumService forum() {
        return forumService;
    }

    @Override
    public IImageService images() {
        return imageService;
    }
}
