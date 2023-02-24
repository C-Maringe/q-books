package com.qbook.app.application.configuration.exception;

import lombok.Getter;

public class ResourceClientResponseException extends BusinessException {
    public ResourceClientResponseException(String message) {
        super(message,"Rest Call Exception", false);
    }
}
