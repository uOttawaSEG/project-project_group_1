/**
 * PasswordHasher is a small utility for salted password hashing.
 * It generates a 16-byte random salt and computes SHA-256 over
 * (salt || UTF-8 password), storing credentials as:
 *   base64(salt) + "$" + base64(hash)
 * The matches(...) method recomputes the hash with the stored salt
 * and compares results in (near) constant time.
 */

package com.example.otams.util;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;

public final class PasswordHasher {
    private PasswordHasher(){}

    public static String hash(String raw) {
        try {
            byte[] salt = new byte[16];
            new SecureRandom().nextBytes(salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] digest = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(salt, Base64.NO_WRAP) + "$" +
                    Base64.encodeToString(digest, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean matches(String raw, String stored) {
        try {
            String[] parts = stored.split("\\$");
            if (parts.length != 2) return false;
            byte[] salt = Base64.decode(parts[0], Base64.NO_WRAP);
            byte[] expect = Base64.decode(parts[1], Base64.NO_WRAP);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] got = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            if (got.length != expect.length) return false;
            int diff = 0;
            for (int i = 0; i < got.length; i++) diff |= got[i] ^ expect[i];
            return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }
}
