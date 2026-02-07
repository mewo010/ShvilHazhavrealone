package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.services.DatabaseCallback;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseDatabaseService<T> {

    protected final DatabaseReference databaseReference;

    public BaseDatabaseService(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    protected void create(DatabaseReference ref, @NonNull String id, @NonNull T data, @Nullable DatabaseCallback<Void> callback) {
        ref.child(id).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    protected void getById(DatabaseReference ref, @NonNull String id, @NonNull Class<T> clazz, @NonNull DatabaseCallback<T> callback) {
        ref.child(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                T data = task.getResult().getValue(clazz);
                callback.onCompleted(data);
            } else {
                callback.onFailed(task.getException());
            }
        });
    }

    protected void getAll(DatabaseReference ref, @NonNull Class<T> clazz, @NonNull DatabaseCallback<List<T>> callback) {
        ref.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<T> dataList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    T data = snapshot.getValue(clazz);
                    dataList.add(data);
                }
                callback.onCompleted(dataList);
            } else {
                callback.onFailed(task.getException());
            }
        });
    }

    protected void update(DatabaseReference ref, @NonNull String id, @NonNull T data, @Nullable DatabaseCallback<Void> callback) {
        ref.child(id).setValue(data).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    protected void delete(DatabaseReference ref, @NonNull String id, @Nullable DatabaseCallback<Void> callback) {
        ref.child(id).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    public String generateId(DatabaseReference ref) {
        return ref.push().getKey();
    }
}
