package model;

public abstract class User {
    private int id;
    private String username;
    private String hashPassword;
    private UserDetails userDetails;

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
}
