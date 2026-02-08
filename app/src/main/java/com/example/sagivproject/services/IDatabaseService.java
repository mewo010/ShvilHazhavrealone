package com.example.sagivproject.services;

/**
 * A central interface for accessing all data services.
 */
public interface IDatabaseService {
    /**
     * @return The auth service.
     */
    IAuthService getAuthService();

    /**
     * @return The user service.
     */
    IUserService getUserService();

    /**
     * @return The medication service.
     */
    IMedicationService getMedicationService();

    /**
     * @return The game service.
     */
    IGameService getGameService();

    /**
     * @return The stats service.
     */
    IStatsService getStatsService();

    /**
     * @return The forum service.
     */
    IForumService getForumService();

    /**
     * @return The image service.
     */
    IImageService getImageService();

    /**
     * @return The forum category service.
     */
    IForumCategoriesService getForumCategoriesService();

    interface DatabaseCallback<T> {
        /// called when the operation completes successfully
        ///
        /// @param object the result of the operation, or null for void operations
        void onCompleted(T object);

        /// called when the operation fails
        ///
        /// @param e the exception describing what went wrong
        void onFailed(Exception e);
    }
}