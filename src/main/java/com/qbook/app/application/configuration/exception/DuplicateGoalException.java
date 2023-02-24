package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class DuplicateGoalException extends BusinessException {

    public DuplicateGoalException(@NonNls String message) {
        super(message, "Duplicate Goal Error", true);
    }
}
