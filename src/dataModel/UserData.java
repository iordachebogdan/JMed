package dataModel;

import model.User;

public class UserData {
    private final int id;
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String bio;
    private final String email;

    public UserData(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getUserDetails().getFirstName();
        this.lastName = user.getUserDetails().getLastName();
        this.bio = user.getUserDetails().getBio();
        this.email = user.getUserDetails().getEmail();
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBio() {
        return bio;
    }

    public String getEmail() {
        return email;
    }
}
