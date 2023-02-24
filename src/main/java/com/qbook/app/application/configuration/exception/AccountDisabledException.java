package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class AccountDisabledException extends BusinessException {

	public AccountDisabledException(@NonNls String message) {
		super(message, "Account Error", false);
	}
}
