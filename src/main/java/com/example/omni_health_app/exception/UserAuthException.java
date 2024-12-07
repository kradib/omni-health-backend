package com.example.omni_health_app.exception;

public class UserAuthException extends Exception {

    public UserAuthException(final String message) {
        super(message);
    }
    public UserAuthException(final String message,final Throwable throwable) {
        super(message, throwable);
    }

}
