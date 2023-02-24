package com.qbook.app.application.configuration.exception;

public class InvalidDayException extends BusinessException {
    public InvalidDayException(String message) {
        super(message, "Booking Error", true);
    }
}
