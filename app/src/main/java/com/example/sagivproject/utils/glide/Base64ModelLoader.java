package com.example.sagivproject.utils.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.nio.ByteBuffer;

/**
 * A custom Glide {@link ModelLoader} for handling Base64 encoded image strings.
 * <p>
 * This loader checks if a string model is a data URI for an image (e.g., "data:image/jpeg;base64,...").
 * If it is, it uses a {@link Base64DataFetcher} to decode the string and provide the image data as a ByteBuffer.
 * </p>
 */
public class Base64ModelLoader implements ModelLoader<String, ByteBuffer> {

    /**
     * Builds the data loading object for the given Base64 model.
     *
     * @param model   The Base64 string model.
     * @param width   The target width.
     * @param height  The target height.
     * @param options The load options.
     * @return A {@link LoadData} object containing the data fetcher.
     */
    @Override
    public LoadData<ByteBuffer> buildLoadData(@NonNull String model, int width, int height, @NonNull Options options) {
        // Strip the data URI prefix to get the pure Base64 content.
        String base64 = model.substring(model.indexOf(',') + 1);
        return new LoadData<>(new ObjectKey(model), new Base64DataFetcher(base64));
    }

    /**
     * Determines if this loader can handle the given model.
     *
     * @param model The model to check.
     * @return True if the model is a data URI for an image, false otherwise.
     */
    @Override
    public boolean handles(@NonNull String model) {
        return model.startsWith("data:image");
    }

    /**
     * The factory class for creating instances of {@link Base64ModelLoader}.
     */
    public static class Factory implements ModelLoaderFactory<String, ByteBuffer> {
        @NonNull
        @Override
        public ModelLoader<String, ByteBuffer> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new Base64ModelLoader();
        }

        @Override
        public void teardown() {
            // No-op
        }
    }
}
