package com.qbook.app.application.configuration.exception;

public class ResourceClientErrorHandlerException extends BusinessException {
    public ResourceClientErrorHandlerException(final String exceptionMessage) {
        super(exceptionMessage, "Rest Client Exception", false);
    }
}
