package com.example.sagivproject.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * A utility class for simple Base64 encoding and decoding.
 * <p>
 * This is used to provide a basic level of obfuscation for sensitive strings,
 * such as API keys, that are stored in the application's source code.
 * </p>
 */
public class EncryptionAPIKey {
    /**
     * Encodes a string into its Base64 representation.
     *
     * @param apiKey The raw string to encode.
     * @return The Base64 encoded string.
     */
    public static String encode(String apiKey) {
        return Base64.getEncoder().encodeToString(apiKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a Base64 encoded string back to its original form.
     *
     * @param encodedKey The Base64 encoded string.
     * @return The original, decoded string.
     */
    public static String decode(String encodedKey) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedKey);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }
}
