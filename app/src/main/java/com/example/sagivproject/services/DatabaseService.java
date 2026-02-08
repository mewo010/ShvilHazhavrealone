package com.example.sagivproject.services;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class DatabaseService implements IDatabaseService {
    private final IAuthService authService;
    private final IUserService userService;
    private final IMedicationService medicationService;
    private final IGameService gameService;
    private final IStatsService statsService;
    private final IForumService forumService;
    private final IImageService imageService;
    private final IForumCategoriesService forumCategoriesService;

    @Inject
    public DatabaseService(
            IAuthService authService,
            IUserService userService,
            IMedicationService medicationService,
            IGameService gameService,
            IStatsService statsService,
            IForumService forumService,
            IImageService imageService,
            IForumCategoriesService forumCategoriesService
    ) {
        this.authService = authService;
        this.userService = userService;
        this.medicationService = medicationService;
        this.gameService = gameService;
        this.statsService = statsService;
        this.forumService = forumService;
        this.imageService = imageService;
        this.forumCategoriesService = forumCategoriesService;
    }

    @Override
    public IAuthService getAuthService() {
        return authService;
    }

    @Override
    public IUserService getUserService() {
        return userService;
    }

    @Override
    public IMedicationService getMedicationService() {
        return medicationService;
    }

    @Override
    public IGameService getGameService() {
        return gameService;
    }

    @Override
    public IStatsService getStatsService() {
        return statsService;
    }

    @Override
    public IForumService getForumService() {
        return forumService;
    }

    @Override
    public IImageService getImageService() {
        return imageService;
    }

    @Override
    public IForumCategoriesService getForumCategoriesService() {
        return forumCategoriesService;
    }
}