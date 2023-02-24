package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class InvalidStartDateException extends BusinessException {
    public InvalidStartDateException(@NonNls String message) {
        super(message, "Booking Error", true);
    }
}
