package com.example.sagivproject.services.impl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.ImageData;
import com.example.sagivproject.services.IImageService;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import javax.inject.Inject;

public class ImageServiceImpl extends BaseDatabaseService<ImageData> implements IImageService {
    private static final String IMAGES_PATH = "images";
    private final DatabaseReference imagesRef;

    @Inject
    public ImageServiceImpl(DatabaseReference databaseReference) {
        super(databaseReference);
        this.imagesRef = databaseReference.child(IMAGES_PATH);
    }

    @Override
    public void getAllImages(DatabaseCallback<List<ImageData>> callback) {
        super.getAll(imagesRef, ImageData.class, callback);
    }

    @Override
    public void createImage(@NonNull ImageData image, @Nullable DatabaseCallback<Void> callback) {
        super.create(imagesRef, image.getId(), image, callback);
    }

    @Override
    public void updateAllImages(List<ImageData> list, DatabaseCallback<Void> callback) {
        imagesRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < list.size(); i++) {
                    ImageData img = list.get(i);
                    imagesRef.child(img.getId()).setValue(img);
                }
                if (callback != null) callback.onCompleted(null);
            } else if (callback != null) {
                callback.onFailed(task.getException());
            }
        });
    }
}