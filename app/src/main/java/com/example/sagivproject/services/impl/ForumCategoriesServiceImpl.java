package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;

import com.example.sagivproject.models.ForumCategory;
import com.example.sagivproject.services.IDatabaseService;
import com.example.sagivproject.services.IForumCategoriesService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class ForumCategoriesServiceImpl extends BaseDatabaseService<ForumCategory> implements IForumCategoriesService {
    private static final String CATEGORIES_PATH = "forum_categories";
    private static final String MESSAGES_PATH = "forum_messages"; // For cleaning up messages on category deletion

    @Inject
    public ForumCategoriesServiceImpl() {
        super(CATEGORIES_PATH, ForumCategory.class);
    }

    @Override
    public void getCategories(IDatabaseService.DatabaseCallback<List<ForumCategory>> callback) {
        databaseReference.child(CATEGORIES_PATH).orderByChild("creationDate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ForumCategory> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ForumCategory category = child.getValue(ForumCategory.class);
                    list.add(category);
                }
                Collections.reverse(list); // Show newest first
                if (callback != null) callback.onCompleted(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (callback != null) callback.onFailed(error.toException());
            }
        });
    }

    @Override
    public void addCategory(String name, IDatabaseService.DatabaseCallback<Void> callback) {
        String categoryId = super.generateId();
        ForumCategory category = new ForumCategory(categoryId, name);
        super.create(category, callback);
    }

    @Override
    public void deleteCategory(String categoryId, IDatabaseService.DatabaseCallback<Void> callback) {
        // First, delete all messages associated with this category to prevent orphaned data.
        deleteData(MESSAGES_PATH + "/" + categoryId, new IDatabaseService.DatabaseCallback<>() {
            @Override
            public void onCompleted(Void result) {
                // After the messages are successfully deleted, delete the category entry itself.
                // This uses the default path ("forum_categories") configured in the constructor.
                delete(categoryId, callback);
            }

            @Override
            public void onFailed(Exception e) {
                // If deleting the messages fails, report the error for the whole operation.
                if (callback != null) {
                    callback.onFailed(e);
                }
            }
        });
    }
}
