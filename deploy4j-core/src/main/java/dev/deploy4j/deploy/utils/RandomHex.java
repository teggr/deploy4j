package dev.deploy4j.deploy.utils;

import java.security.SecureRandom;

public class RandomHex {
    private static final SecureRandom secureRandom = new SecureRandom();

    public static String randomHex(int byteLength) {
        byte[] bytes = new byte[byteLength];
        secureRandom.nextBytes(bytes);

        StringBuilder sb = new StringBuilder(byteLength * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        System.out.println(randomHex(8)); // => 16 hex chars, like "4a1f9c82d4e5b0fa"
    }
}