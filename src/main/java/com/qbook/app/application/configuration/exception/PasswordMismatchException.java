package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class PasswordMismatchException extends BusinessException {
	public PasswordMismatchException(@NonNls String message) {
		super(message, "Authentication Error", true);
	}
}
