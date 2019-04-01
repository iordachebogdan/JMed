package controller;

public class NotAuthorizedException extends Exception {
    NotAuthorizedException(String message) {
        super(message);
    }
}
