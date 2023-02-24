package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class ProductUpdateException extends BusinessException {

	public ProductUpdateException(@NonNls String message) {
		super(message, "Product Error", false);
	}
}
