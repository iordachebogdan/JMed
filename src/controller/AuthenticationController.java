package controller;

import dataModel.UserData;
import model.*;
import service.AuthenticationService;
import service.LoggingService;
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

    public UserTypeEnum getType(String token) {
        User user = AuthenticationService.getInstance().findUserByToken(token);
        if (user instanceof Medic)
            return UserTypeEnum.MEDIC;
        else if (user instanceof Patient)
            return UserTypeEnum.PATIENT;
        else
            return null;
    }

    public UserData registerUser(String username, String password,
                                 String firstName, String lastName, String email, String bio,
                                 UserTypeEnum type) {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.CREATE,
                this.getClass().getName(),
                String.format("User tries to register username=%s", username)
        );

        int id;
        try {
            id = UserService.getInstance().registerUser(username, password,
                    new UserDetails(firstName, lastName, email, bio),
                    type);
        }
        catch (Exception ex) {
            LoggingService.getInstance().log(entry, username, LoggingService.Status.BAD_REQUEST);
            throw ex;
        }

        LoggingService.getInstance().log(entry, username, LoggingService.Status.OK);
        return new UserData(UserService.getInstance().getUserById(id));
    }

    public String authenticateUser(String username, String password) throws NotAuthorizedException {
        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.AUTHENTICATE,
                this.getClass().getName(),
                "User tries to login"
        );

        User user = UserService.getInstance().getUserByUsername(username);
        if (user == null) {
            LoggingService.getInstance().log(entry, null, LoggingService.Status.NOT_FOUND);
            throw new IllegalArgumentException("No such user");
        }
        String token = AuthenticationService.getInstance().authenticateUser(user, password);
        if (token == null) {
            LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.UNAUTHORIZED);
            throw new NotAuthorizedException("Invalid password");
        }
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);
        return token;
    }

    public void logOutUser(String token) {
        User user = AuthenticationService.getInstance().findUserByToken(token);
        if (user == null)
            return;
        AuthenticationService.getInstance().logOutUser(user, token);

        LoggingService.LogEntry entry = new LoggingService.LogEntry(
                LoggingService.Operation.DELETE,
                this.getClass().getName(),
                "User logout"
        );
        LoggingService.getInstance().log(entry, user.getUsername(), LoggingService.Status.OK);

    }
}
