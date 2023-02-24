package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class DayAlreadyCashedUpException extends BusinessException {

	public DayAlreadyCashedUpException(@NonNls String message) {
		super(message, "Cash Up Error", false);
	}
}
