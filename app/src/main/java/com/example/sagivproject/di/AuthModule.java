package com.example.sagivproject.di;

import com.example.sagivproject.services.IAuthService;
import com.example.sagivproject.services.IForumCategoriesService;
import com.example.sagivproject.services.IForumService;
import com.example.sagivproject.services.IGameService;
import com.example.sagivproject.services.IImageService;
import com.example.sagivproject.services.IMedicationService;
import com.example.sagivproject.services.IStatsService;
import com.example.sagivproject.services.IUserService;
import com.example.sagivproject.services.impl.AuthServiceImpl;
import com.example.sagivproject.services.impl.ForumCategoriesServiceImpl;
import com.example.sagivproject.services.impl.ForumServiceImpl;
import com.example.sagivproject.services.impl.GameServiceImpl;
import com.example.sagivproject.services.impl.ImageServiceImpl;
import com.example.sagivproject.services.impl.MedicationServiceImpl;
import com.example.sagivproject.services.impl.StatsServiceImpl;
import com.example.sagivproject.services.impl.UserServiceImpl;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * A Hilt module that provides bindings for all the application's service interfaces.
 * <p>
 * This abstract class uses the {@code @Binds} annotation to tell Hilt which implementation to use
 * when an interface is requested. For example, when {@code IAuthService} is injected, Hilt will provide
 * an instance of {@code AuthServiceImpl}.
 * </p>
 */
@Module
@InstallIn(SingletonComponent.class)
public abstract class AuthModule {

    @Binds
    @Singleton
    public abstract IAuthService bindAuthService(AuthServiceImpl authService);

    @Binds
    @Singleton
    public abstract IUserService bindUserService(UserServiceImpl userService);

    @Binds
    @Singleton
    public abstract IMedicationService bindMedicationService(MedicationServiceImpl medicationService);

    @Binds
    @Singleton
    public abstract IStatsService bindStatsService(StatsServiceImpl statsService);

    @Binds
    @Singleton
    public abstract IForumService bindForumService(ForumServiceImpl forumService);

    @Binds
    @Singleton
    public abstract IForumCategoriesService bindForumCategoryService(ForumCategoriesServiceImpl forumCategoryService);

    @Binds
    @Singleton
    public abstract IGameService bindGameService(GameServiceImpl gameService);

    @Binds
    @Singleton
    public abstract IImageService bindImageService(ImageServiceImpl imageService);
}
