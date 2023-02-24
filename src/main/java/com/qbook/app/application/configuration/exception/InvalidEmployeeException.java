package com.qbook.app.application.configuration.exception;

public class InvalidEmployeeException extends BusinessException {
    public InvalidEmployeeException(String message) {
        super(message, "Booking Error", true);
    }
}
