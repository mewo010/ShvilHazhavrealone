package com.example.sagivproject.services.interfaces;

/**
 * A generic callback interface for database operations.
 *
 * @param <T> The type of data expected on completion.
 */
public interface DatabaseCallback<T> {
    /**
     * Called when the database operation is completed successfully.
     *
     * @param object The result of the operation.
     */
    void onCompleted(T object);

    /**
     * Called when the database operation fails.
     *
     * @param e The exception that occurred.
     */
    void onFailed(Exception e);
}
