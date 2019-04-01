package model;

import java.util.ArrayList;
import java.util.List;

public abstract class User implements Authenticable {
    private int id;
    private String username;
    private String hashPassword;
    private UserDetails userDetails;
    private String token;

    private List<Case> cases;

    public User(int id, String username, String hashPassword, UserDetails userDetails) {
        this.id = id;
        this.username = username;
        this.hashPassword = hashPassword;
        this.userDetails = userDetails;
        this.cases = new ArrayList<>();
    }

    public abstract UserTypeEnum getType();

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String hashPassword) {
        this.hashPassword = hashPassword;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    @Override
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    void addCase(Case c) {
        cases.add(c);
    }

    public List<Case> getAssignedCases() {
        return cases;
    }
}
