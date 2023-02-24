package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class InvalidBusinessConfigurationException extends BusinessException {

	public InvalidBusinessConfigurationException(@NonNls String message) {
		super(message, "Configuration Error", true);
	}
}
