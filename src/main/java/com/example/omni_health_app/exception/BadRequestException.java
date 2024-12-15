package com.example.omni_health_app.exception;

public class BadRequestException extends Exception {

    public BadRequestException(final String message) {
        super(message);
    }
    public BadRequestException(final String message,final Throwable throwable) {
        super(message, throwable);
    }
}
