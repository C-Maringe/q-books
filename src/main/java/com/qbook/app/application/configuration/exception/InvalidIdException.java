package com.qbook.app.application.configuration.exception;

public class InvalidIdException extends BusinessException {
    public InvalidIdException(String message) {
        super(message, "Booking Error", true);
    }
}
