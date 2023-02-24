package com.qbook.app.application.configuration.exception;

public class ResourceNotFoundException extends BusinessException {
    public ResourceNotFoundException(String message) {
        super(message, "Resource Error", true);
    }
}
