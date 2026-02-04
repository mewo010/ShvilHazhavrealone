package com.example.sagivproject.bases;

import com.example.sagivproject.services.interfaces.ITable;
import com.google.firebase.database.DatabaseReference;

import java.util.Map;

public abstract class BaseTable<T> implements ITable<T> {
    protected DatabaseReference dbRef;
    private Class<T> type;

    public BaseTable(DatabaseReference ref, Class<T> type) {
        this.dbRef = ref;
        this.type = type;
    }

    @Override
    public void create(String id, T data) {
        dbRef.child(id).setValue(data);
    }

    @Override
    public void read(String id, OnDataFetched<T> callback) {
        dbRef.child(id).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess(task.getResult().getValue(type));
            } else {
                callback.onFailure(task.getException());
            }
        });
    }

    @Override
    public void update(String id, Map<String, Object> updates) {
        dbRef.child(id).updateChildren(updates);
    }

    @Override
    public void delete(String id) {
        dbRef.child(id).removeValue();
    }
}