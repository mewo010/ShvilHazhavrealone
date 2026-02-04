package com.example.sagivproject.services.interfaces;

import java.util.Map;

public interface ITable<T> {
    void create(String id, T data);
    void read(String id, OnDataFetched<T> callback);
    void update(String id, Map<String, Object> updates);
    void delete(String id);

    // ממשק פנימי לקבלת נתונים אסינכרונית
    interface OnDataFetched<T> {
        void onSuccess(T data);
        void onFailure(Exception e);
    }
}