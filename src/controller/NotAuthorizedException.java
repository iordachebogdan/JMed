package controller;

class NotAuthorizedException extends Exception {
    NotAuthorizedException(String message) {
        super(message);
    }
}
