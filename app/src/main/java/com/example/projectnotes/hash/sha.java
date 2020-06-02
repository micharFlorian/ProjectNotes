package com.example.projectnotes.hash;

import java.security.MessageDigest;

/**
 * Clase que permite hashear
 */
public class sha {

    /**
     * Convierte un byte[] en un hash  (SHA-1,SHA-224,SHA-256,SHA-384,SHA-512)
     */
    public static byte[] encryptSHA(byte[] data, String shaN) throws Exception {

        MessageDigest sha = MessageDigest.getInstance(shaN);
        sha.update(data);
        return sha.digest();
    }
}
