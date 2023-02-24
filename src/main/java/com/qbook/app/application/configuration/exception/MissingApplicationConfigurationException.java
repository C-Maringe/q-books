package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class MissingApplicationConfigurationException extends BusinessException {

	public MissingApplicationConfigurationException(@NonNls String message) {
		super(message, "Configuration Error", true);
	}
}
