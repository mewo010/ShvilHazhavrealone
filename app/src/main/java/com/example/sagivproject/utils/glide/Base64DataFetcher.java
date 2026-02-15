package com.example.sagivproject.utils.glide;

import android.util.Base64;

import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.nio.ByteBuffer;

/**
 * A custom Glide {@link DataFetcher} for decoding Base64 strings.
 * <p>
 * This class takes the raw Base64 string, decodes it into a byte array,
 * and wraps it in a {@link ByteBuffer} for Glide to process.
 * </p>
 */
public class Base64DataFetcher implements DataFetcher<ByteBuffer> {
    private final String model;

    public Base64DataFetcher(String model) {
        this.model = model;
    }

    /**
     * Decodes the Base64 string and provides the resulting data to the callback.
     *
     * @param priority The priority of the request.
     * @param callback The callback to be invoked with the loaded data.
     */
    @Override
    public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super ByteBuffer> callback) {
        byte[] data = Base64.decode(model, Base64.DEFAULT);
        callback.onDataReady(ByteBuffer.wrap(data));
    }

    @Override
    public void cleanup() {
        // No-op
    }

    @Override
    public void cancel() {
        // No-op
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
