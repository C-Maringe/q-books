package com.qbook.app.application.configuration.exception.applicationConfigurationExceptions;

import com.qbook.app.application.configuration.exception.BusinessException;
import org.jetbrains.annotations.NonNls;

public class ConfigurationTestEmailException extends BusinessException {

	public ConfigurationTestEmailException(@NonNls String message) {
		super(message, "Configuration Error", true);
	}
}
