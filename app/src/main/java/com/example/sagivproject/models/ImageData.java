package com.example.sagivproject.models;

import java.io.Serializable;

public class ImageData implements Serializable, Idable {
    private String id;
    private String base64;

    public ImageData() {
    }

    public ImageData(String id, String base64) {
        this.id = id;
        this.base64 = base64;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getBase64() {
        return base64;
    }

    public void setBase64(String base64) {
        this.base64 = base64;
    }
}