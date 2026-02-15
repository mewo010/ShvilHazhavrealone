package com.example.sagivproject.utils.glide;

import android.content.Context;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.module.AppGlideModule;

import java.nio.ByteBuffer;

/**
 * A custom {@link AppGlideModule} to register custom model loaders with Glide.
 * <p>
 * This module registers a custom factory for handling Base64 encoded strings, allowing Glide
 * to load images directly from them.
 * </p>
 */
@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        // Register the custom Base64ModelLoader to handle loading images from Base64 strings.
        registry.prepend(String.class, ByteBuffer.class, new Base64ModelLoader.Factory());
    }
}
