package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class DuplicateUserException extends BusinessException {

    public DuplicateUserException(@NonNls String message) {
        super(message, "Registration Error", true);
    }
}
