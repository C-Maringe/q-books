package com.qbook.app.application.configuration.exception;

public class TreatmentException extends BusinessException {
    public TreatmentException(String message) {
        super(message,"Treatment Exception", true);
    }
}
