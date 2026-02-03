package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.ImageData;
import com.example.sagivproject.services.interfaces.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IImageService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ImageService implements IImageService {

    private static final String IMAGES_PATH = "images";
    private final DatabaseReference databaseReference;

    @Inject
    public ImageService(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference.child(IMAGES_PATH);
    }

    @Override
    public void getAllImages(DatabaseCallback<List<ImageData>> callback) {
        databaseReference.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<ImageData> imageList = new ArrayList<>();
                for (DataSnapshot snapshot : task.getResult().getChildren()) {
                    ImageData imageData = snapshot.getValue(ImageData.class);
                    imageList.add(imageData);
                }
                callback.onCompleted(imageList);
            } else {
                callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void createImage(@NonNull ImageData image, @Nullable DatabaseCallback<Void> callback) {
        databaseReference.child(image.getId()).setValue(image).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                if (callback != null) callback.onCompleted(null);
            } else {
                if (callback != null) callback.onFailed(task.getException());
            }
        });
    }

    @Override
    public void updateAllImages(List<ImageData> list, DatabaseCallback<Void> callback) {
        databaseReference.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (int i = 0; i < list.size(); i++) {
                    ImageData img = list.get(i);
                    databaseReference.child(img.getId()).setValue(img);
                }
                if (callback != null) callback.onCompleted(null);
            } else if (callback != null) {
                callback.onFailed(task.getException());
            }
        });
    }
}
