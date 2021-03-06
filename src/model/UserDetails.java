package model;

import java.io.Serializable;

public class UserDetails implements Serializable {
    private String firstName;
    private String lastName;
    private String email;
    private String bio;

    public UserDetails(String firstName, String lastName, String email, String bio) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.bio = bio;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
