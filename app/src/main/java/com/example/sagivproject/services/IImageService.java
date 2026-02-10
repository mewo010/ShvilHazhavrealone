package com.example.sagivproject.services;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.sagivproject.models.ImageData;
import com.example.sagivproject.services.IDatabaseService.DatabaseCallback;

import java.util.List;


public interface IImageService {
    /**
     * get all images from the database
     *
     * @param callback the callback to call when the operation is completed
     */
    void getAllImages(DatabaseCallback<List<ImageData>> callback);

    /**
     * create a new image in the database
     *
     * @param image    the image object to create
     * @param callback the callback to call when the operation is completed
     */
    void createImage(@NonNull ImageData image, @Nullable DatabaseCallback<Void> callback);

    /**
     * update all images in the database
     *
     * @param list     the list of images to update
     * @param callback the callback to call when the operation is completed
     */
    void updateAllImages(List<ImageData> list, DatabaseCallback<Void> callback);
}
