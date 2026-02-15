package com.example.sagivproject.utils.glide;

/**
 * A simple model class to wrap a Base64 string.
 * <p>
 * Note: This class may be unused, as the custom Glide implementation appears to handle
 * raw Base64 strings directly.
 * </p>
 */
public class Base64Model {
    private final String base64;

    public Base64Model(String base64) {
        this.base64 = base64;
    }

    public String getBase64() {
        return base64;
    }
}
