package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class ProductRegistrationException extends BusinessException {

	public ProductRegistrationException(@NonNls String message) {
		super(message, "Product Error", false);
	}
}
