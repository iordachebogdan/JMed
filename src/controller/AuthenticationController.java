package controller;

import dataModel.UserData;
import model.Patient;
import model.User;
import model.UserDetails;
import model.UserTypeEnum;
import service.AuthenticationService;
import service.UserService;

public class AuthenticationController {
    private static AuthenticationController instance = null;

    //singleton pattern
    private AuthenticationController() {}

    public static AuthenticationController getInstance() {
        if (instance == null)
            instance = new AuthenticationController();
        return instance;
    }

    public UserData registerUser(String username, String password,
                                 String firstName, String lastName, String email, String bio,
                                 UserTypeEnum type) {
        int id = UserService.getInstance().registerUser(username, password,
                new UserDetails(firstName, lastName, email, bio),
                type);
        return new UserData(UserService.getInstance().getUserById(id));
    }

    public String authenticateUser(String username, String password) throws NotAuthorizedException {
        User user = UserService.getInstance().getUserByUsername(username);
        if (user == null)
            throw new IllegalArgumentException("No such user");
        String token = AuthenticationService.getInstance().authenticateUser(user, password);
        if (token == null)
            throw new NotAuthorizedException("Invalid password");
        return token;
    }

    public void logOutUser(String token) {
        User user = AuthenticationService.getInstance().findUserByToken(token);
        if (user == null)
            return;
        AuthenticationService.getInstance().logOutUser(user, token);
    }
}
