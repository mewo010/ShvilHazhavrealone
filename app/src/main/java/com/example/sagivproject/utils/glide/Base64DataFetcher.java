package com.example.sagivproject.utils.glide;

import android.util.Base64;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.nio.ByteBuffer;

public class Base64DataFetcher implements DataFetcher<ByteBuffer> {
    private final String model;

    public Base64DataFetcher(String model) {
        this.model = model;
    }

    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super ByteBuffer> callback) {
        byte[] data = Base64.decode(model, Base64.DEFAULT);
        callback.onDataReady(ByteBuffer.wrap(data));
    }

    @Override
    public void cleanup() {
    }

    @Override
    public void cancel() {
    }

    @NonNull
    @Override
    public Class<ByteBuffer> getDataClass() {
        return ByteBuffer.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.LOCAL;
    }
}