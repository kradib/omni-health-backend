package com.example.omni_health_app.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashUtil {


    public static String hashWithSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing key", e);
        }
    }

    public static boolean isHashMatch(String rawKey, String hashedSecret) {
        return hashWithSHA256(rawKey).equals(hashedSecret);
    }

}
