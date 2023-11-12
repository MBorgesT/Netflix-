package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class AuthUtil {

    private static AuthUtil instance;
    private static int NCHARS = 30;
    private static HashMap<String, String> hexAuths;

    private AuthUtil() {
        hexAuths = new HashMap<>();
    }

    public static AuthUtil getInstance() {
        if (instance == null) {
            instance = new AuthUtil();
        }
        return instance;
    }

    public String newRandomHexString(String username){
        Random r = new Random();
        StringBuffer sb = new StringBuffer();
        while(sb.length() < NCHARS){
            sb.append(Integer.toHexString(r.nextInt()));
        }

        String newAuth = sb.toString().substring(0, NCHARS);
        hexAuths.put(username, newAuth);
        return newAuth;
    }

    public boolean isCodeAuthorized(String code) {
        return hexAuths.containsValue(code);
    }

}
