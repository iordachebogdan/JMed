package model;

public abstract class User implements Authenticable {
    private int id;
    private String username;
    private String hashPassword;
    private UserDetails userDetails;
    private String token;

    public User(int id, String username, String hashPassword, UserDetails userDetails) {
        this.id = id;
        this.username = username;
        this.hashPassword = hashPassword;
        this.userDetails = userDetails;
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
}
