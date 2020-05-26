package com.example.projectnotes.hash;

import java.security.MessageDigest;

public class sha {

    /**
     * @param shaN encrypt method,SHA-1,SHA-224,SHA-256,SHA-384,SHA-512
     */
    public static byte[] encryptSHA(byte[] data, String shaN) throws Exception {

        MessageDigest sha = MessageDigest.getInstance(shaN);
        sha.update(data);
        return sha.digest();
    }
}
