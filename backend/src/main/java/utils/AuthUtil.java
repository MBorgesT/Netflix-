package utils;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthUtil {

    public static byte[] hashPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Add password bytes to digest
        md.update(password.getBytes());

        // Get the hash's bytes
        return md.digest();
    }

}
