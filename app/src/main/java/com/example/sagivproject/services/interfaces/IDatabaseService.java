package com.example.sagivproject.services.interfaces;

/**
 * A central interface for accessing all data services.
 */
public interface IDatabaseService {
    /**
     * @return The user service.
     */
    IUserService users();

    /**
     * @return The medication service.
     */
    IMedicationService medications();

    /**
     * @return The game service.
     */
    IGameService games();

    /**
     * @return The stats service.
     */
    IStatsService stats();

    /**
     * @return The forum service.
     */
    IForumService forum();

    /**
     * @return The image service.
     */
    IImageService images();
}
