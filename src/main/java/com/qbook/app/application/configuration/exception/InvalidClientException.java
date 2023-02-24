package com.qbook.app.application.configuration.exception;

public class InvalidClientException extends BusinessException {
    public InvalidClientException(String message) {
        super(message, "Client Search Error", true);
    }
}
