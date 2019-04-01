package service;

import model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

public class AuthenticationService {
    private static AuthenticationService instance = null;

    private Map<String, User> tokenTable;

    //singleton pattern
    private AuthenticationService() {
        tokenTable = new TreeMap<>();
    }

    public static AuthenticationService getInstance() {
        if (instance == null)
            instance = new AuthenticationService();
        return instance;
    }

    static String hashFunction(String password) {
        byte[] digest;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            digest = md.digest();
        } catch(NoSuchAlgorithmException exp) {
            digest = password.getBytes();
        }
        return new String(digest, StandardCharsets.UTF_8);
    }

    public boolean isUserAuthenticated(User user, String token) {
        return (tokenTable.get(token) == user);
    }

    public String authenticateUser(User user, String password) {
        if (!hashFunction(password).equals(user.getHashPassword()))
            return null;
        Random random = new Random();
        String token = random.ints(48,122)
                .mapToObj(i -> (char) i)
                .limit(50)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                .toString();
        tokenTable.put(token, user);
        user.setToken(token);
        return token;
    }

    public void logOutUser(User user, String token) {
        if (tokenTable.get(token) != user)
            return;
        tokenTable.remove(token);
    }

    public User findUserByToken(String token) {
        return tokenTable.get(token);
    }
}
