package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class InvalidCashUpDayException extends BusinessException {

	public InvalidCashUpDayException(@NonNls String message) {
		super(message, "Cash Up Error", true);
	}
}
