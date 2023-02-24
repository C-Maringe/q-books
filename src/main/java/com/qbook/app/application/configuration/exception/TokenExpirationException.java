package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class TokenExpirationException extends BusinessException {
	public TokenExpirationException(@NonNls String message) {
		super(message, "Authentication Error", true);
	}
}
