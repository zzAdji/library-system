package com.library.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordUtils {

    // Constructeur privé pour empêcher l'instanciation (règle des classes utilitaires)
    private PasswordUtils() {
        throw new UnsupportedOperationException("Cette classe ne peut pas être instanciée");
    }

    /**
     * Hache un mot de passe en clair en utilisant SHA-256.
     */
    public static String hash(String rawPassword) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(rawPassword.getBytes());
            return bytesToHex(encodedhash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur lors du hachage du mot de passe", e);
        }
    }

    /**
     * Vérifie si le mot de passe en clair correspond au hash stocké.
     */
    public static boolean verify(String rawPassword, String storedHash) {
        if (rawPassword == null || storedHash == null) {
            return false;
        }
        String generatedHash = hash(rawPassword);
        return generatedHash.equals(storedHash);
    }

    /**
     * Méthode interne pour convertir les bytes en chaîne hexadécimale (ex: "f865b536...")
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}