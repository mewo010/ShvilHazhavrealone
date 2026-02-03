package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.ImageData;
import com.example.sagivproject.services.interfaces.IDatabaseService;
import com.example.sagivproject.services.interfaces.IDatabaseService.DatabaseCallback;
import com.example.sagivproject.services.interfaces.IImageService;

import java.util.List;

import javax.inject.Inject;

public class ImageService implements IImageService {
    private final IDatabaseService databaseService;

    @Inject
    public ImageService(IDatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @Override
    public void getAllImages(DatabaseCallback<List<ImageData>> callback) {
        databaseService.getAllImages(callback);
    }

    @Override
    public void createImage(@NonNull ImageData image, @Nullable DatabaseCallback<Void> callback) {
        databaseService.createImage(image, callback);
    }

    @Override
    public void updateAllImages(List<ImageData> list, DatabaseCallback<Void> callback) {
        databaseService.updateAllImages(list, callback);
    }
}
