package com.example.sagivproject.services.interfaces;

public interface DatabaseCallback<T> {
    void onCompleted(T object);

    void onFailed(Exception e);
}
