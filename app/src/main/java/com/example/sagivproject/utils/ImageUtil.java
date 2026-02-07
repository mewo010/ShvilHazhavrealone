package com.example.sagivproject.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;

public class ImageUtil {
    private static final String BASE64_PREFIX = "data:image/jpeg;base64,";

    public static @Nullable String convertTo64Base(@NotNull final ImageView postImage) {
        if (postImage.getDrawable() == null) {
            return null;
        }
        Bitmap bitmap = ((BitmapDrawable) postImage.getDrawable()).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream.toByteArray();
        return BASE64_PREFIX + Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public static void loadImage(@NotNull final Context context, @Nullable final String base64Code, @NotNull final ImageView imageView) {
        if (base64Code == null || base64Code.isEmpty()) {
            // Optional: Set a placeholder for empty codes
            // imageView.setImageResource(R.drawable.ic_placeholder);
            return;
        }

        String pureBase64 = base64Code.substring(base64Code.indexOf(",") + 1);
        byte[] decodedString = Base64.decode(pureBase64, Base64.DEFAULT);

        Glide.with(context)
                .load(decodedString)
                .into(imageView);
    }
}