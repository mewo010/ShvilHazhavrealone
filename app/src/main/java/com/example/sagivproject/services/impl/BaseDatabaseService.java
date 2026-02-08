package com.example.sagivproject.services.impl;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.Idable;
import com.example.sagivproject.services.IDatabaseService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

public abstract class BaseDatabaseService<T extends Idable> {
    /// tag for logging
    private static final String TAG = "BaseFirebaseService";

    /// the reference to the database
    private final DatabaseReference databaseReference;

    /// the path in the database for this entity type
    private final String path;

    /// the class of the entity type (needed for Firebase deserialization)
    private final Class<T> clazz;

    protected BaseDatabaseService(@NotNull final String path, @NotNull final Class<T> clazz) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        this.path = path;
        this.clazz = clazz;
    }

    /// generate a new id for a new entity in the database
    ///
    /// @return a new id
    protected String generateId() {
        return databaseReference.child(path).push().getKey();
    }

    /// create or overwrite an entity in the database
    ///
    /// @param item     the entity to create
    /// @param callback the callback to call when the operation is completed
    protected void create(@NotNull final T item, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        writeData(path + "/" + item.getId(), item, callback);
    }

    /// get a single entity from the database by id
    ///
    /// @param id       the id of the entity
    /// @param callback the callback to call when the operation is completed
    protected void get(@NotNull final String id, final IDatabaseService.DatabaseCallback<T> callback) {
        getData(path + "/" + id, callback);
    }

    /// get all entities of this type from the database
    ///
    /// @param callback the callback to call when the operation is completed
    protected void getAll(final IDatabaseService.DatabaseCallback<List<T>> callback) {
        getDataList(path, callback);
    }

    /// delete an entity from the database by id
    ///
    /// @param id       the id of the entity to delete
    /// @param callback the callback to call when the operation is completed
    protected void delete(@NotNull final String id, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        deleteData(path + "/" + id, callback);
    }

    /// update an entity using a transaction
    ///
    /// @param id       the id of the entity to update
    /// @param function the function to apply to the current value
    /// @param callback the callback to call when the operation is completed
    protected void update(@NotNull final String id, final @NotNull UnaryOperator<T> function, @Nullable final IDatabaseService.DatabaseCallback<T> callback) {
        runTransaction(path + "/" + id, function, callback);
    }

    // region low-level helpers

    private DatabaseReference readData(@NotNull final String fullPath) {
        return databaseReference.child(fullPath);
    }

    private void writeData(@NotNull final String fullPath, @NotNull final Object data, final @Nullable IDatabaseService.DatabaseCallback<Void> callback) {
        readData(fullPath).setValue(data, (error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    private void deleteData(@NotNull final String fullPath, @Nullable final IDatabaseService.DatabaseCallback<Void> callback) {
        readData(fullPath).removeValue((error, ref) -> {
            if (error != null) {
                if (callback == null) return;
                callback.onFailed(error.toException());
            } else {
                if (callback == null) return;
                callback.onCompleted(null);
            }
        });
    }

    private void getData(@NotNull final String fullPath, @NotNull final IDatabaseService.DatabaseCallback<T> callback) {
        readData(fullPath).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            T data = task.getResult().getValue(clazz);
            callback.onCompleted(data);
        });
    }

    private void getDataList(@NotNull final String fullPath, @NotNull final IDatabaseService.DatabaseCallback<List<T>> callback) {
        readData(fullPath).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Error getting data", task.getException());
                callback.onFailed(task.getException());
                return;
            }
            List<T> tList = new ArrayList<>();
            task.getResult().getChildren().forEach(dataSnapshot -> {
                T t = dataSnapshot.getValue(clazz);
                tList.add(t);
            });
            callback.onCompleted(tList);
        });
    }

    private void runTransaction(@NotNull final String fullPath, @NotNull final UnaryOperator<T> function, @Nullable final IDatabaseService.DatabaseCallback<T> callback) {
        readData(fullPath).runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                // bug note: currentValue can be null even if the data exists in the database.
                // Firebase will then re-run the transaction with the correct data.
                T currentValue = currentData.getValue(clazz);
                if (currentValue != null) {
                    currentValue = function.apply(currentValue);
                }
                currentData.setValue(currentValue);
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if (error != null) {
                    Log.e(TAG, "Transaction failed", error.toException());
                    if (callback != null) {
                        callback.onFailed(error.toException());
                    }
                    return;
                }
                T result = currentData != null ? currentData.getValue(clazz) : null;
                if (callback != null) {
                    callback.onCompleted(result);
                }
            }
        });
    }

    // endregion low-level helpers
}