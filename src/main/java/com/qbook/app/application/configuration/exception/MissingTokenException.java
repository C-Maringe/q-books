package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class MissingTokenException extends BusinessException {
	public MissingTokenException(@NonNls String message) {
		super(message, "Authentication Error", true);
	}
}
