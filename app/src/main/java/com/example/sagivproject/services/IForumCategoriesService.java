package com.example.sagivproject.services;

import com.example.sagivproject.models.ForumCategory;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;

import java.util.List;

public interface IForumCategoriesService {
    /**
     * Retrieves a list of forum categories from the database.
     *
     * @param callback A callback to be invoked with the list of categories.
     */
    void getCategories(DatabaseCallback<List<ForumCategory>> callback);

    /**
     * Adds a new forum category to the database.
     */
    void addCategory(String name, DatabaseCallback<Void> callback);

    /**
     * Deletes a forum category from the database.
     */
    void deleteCategory(String categoryId, DatabaseCallback<Void> callback);
}