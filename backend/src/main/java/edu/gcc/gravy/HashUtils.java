package edu.gcc.gravy;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;

public class HashUtils {
    public static String sha256(String input) throws NoSuchAlgorithmException {
        // 1. Get SHA-256 MessageDigest instance
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        // 2. Convert string to bytes and compute hash
        byte[] encodedHash = digest.digest(
                input.getBytes(StandardCharsets.UTF_8));

        // 3. Convert byte array to hexadecimal string
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
