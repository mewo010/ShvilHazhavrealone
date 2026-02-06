package com.example.sagivproject.utils.glide;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.signature.ObjectKey;

import java.nio.ByteBuffer;

public class Base64ModelLoader implements ModelLoader<String, ByteBuffer> {

    @Override
    public LoadData<ByteBuffer> buildLoadData(@NonNull String model, int width, int height, @NonNull Options options) {
        String base64 = model.substring(model.indexOf(',') + 1);
        return new LoadData<>(new ObjectKey(model), new Base64DataFetcher(base64));
    }

    @Override
    public boolean handles(@NonNull String model) {
        return model.startsWith("data:image");
    }

    public static class Factory implements ModelLoaderFactory<String, ByteBuffer> {
        @NonNull
        @Override
        public ModelLoader<String, ByteBuffer> build(@NonNull MultiModelLoaderFactory multiFactory) {
            return new Base64ModelLoader();
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
