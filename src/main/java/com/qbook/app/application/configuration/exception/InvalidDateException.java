package com.qbook.app.application.configuration.exception;

public class InvalidDateException extends BusinessException {
    public InvalidDateException(String message) {
        super(message, "Date Error", true);
    }
}
