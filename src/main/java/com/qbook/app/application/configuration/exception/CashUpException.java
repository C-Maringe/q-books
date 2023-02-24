package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class CashUpException extends BusinessException {

	public CashUpException(@NonNls String message) {
		super(message, "Cash Up Error", false);
	}
}
