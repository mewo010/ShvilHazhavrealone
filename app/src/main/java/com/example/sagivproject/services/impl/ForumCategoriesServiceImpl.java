package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;

import com.example.sagivproject.models.ForumCategory;
import com.example.sagivproject.services.IDatabaseService;
import com.example.sagivproject.services.IForumCategoriesService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

public class ForumCategoriesServiceImpl extends BaseDatabaseService<ForumCategory> implements IForumCategoriesService {
    private static final String CATEGORIES_PATH = "forum_categories";
    private final DatabaseReference categoriesRef;

    @Inject
    public ForumCategoriesServiceImpl(DatabaseReference databaseReference) {
        super(databaseReference);
        this.categoriesRef = databaseReference.child(CATEGORIES_PATH);
    }

    @Override
    public void getCategories(IDatabaseService.DatabaseCallback<List<ForumCategory>> callback) {
        categoriesRef.orderByChild("creationDate").addValueEventListener(new ValueEventListener() {
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
        String categoryId = generateId(categoriesRef);
        ForumCategory category = new ForumCategory(categoryId, name);
        super.create(categoriesRef, categoryId, category, callback);
    }

    @Override
    public void deleteCategory(String categoryId, IDatabaseService.DatabaseCallback<Void> callback) {
        super.delete(categoriesRef, categoryId, callback);
    }
}
