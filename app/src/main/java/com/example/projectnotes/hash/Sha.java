package com.example.projectnotes.hash;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Clase que permite hashear
 */
public class Sha {

    /**
     * Convierte un byte[] en un hash  (SHA-1,SHA-224,SHA-256,SHA-384,SHA-512)
     */
    private static byte[] encryptSHA(byte[] data, String shaN) throws Exception {

        MessageDigest sha = MessageDigest.getInstance(shaN);
        sha.update(data);
        return sha.digest();
    }

    /**
     * Convierte un String en Hash
     */
    public static String stringToHash(String string, String SHA) {
        byte[] inputData = string.getBytes();
        byte[] outputData = new byte[0];
        try {
            outputData = Sha.encryptSHA(inputData, SHA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (new BigInteger(1, outputData)).toString(16);
    }
}
