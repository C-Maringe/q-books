package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class NotAuthorisedException extends BusinessException {

	public NotAuthorisedException(@NonNls String message) {
		super(message, "Authorization Error", true);
	}
}
