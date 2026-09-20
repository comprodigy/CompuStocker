package com.shaunwalsh.compustocker.services;

import java.security.MessageDigest;
import java.security.SecureRandom;

public class PasswordHasher {

    public static String newSalt() {
        byte[] bytes = new byte[16];
        new SecureRandom().nextBytes(bytes);
        return toHex(bytes);
    }

    public static String hash(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(salt.getBytes("UTF-8"));
            return toHex(digest.digest(password.getBytes("UTF-8")));
        } catch (Exception e) {
            throw new RuntimeException("Could not hash password", e);
        }
    }

    public static boolean matches(String password, String salt, String expectedHash) {
        if (password == null || salt == null || expectedHash == null) {
            return false;
        }
        return hash(password, salt).equals(expectedHash);
    }

    private static String toHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}