package com.qbook.app.application.configuration.exception.reportingExceptions;

import com.qbook.app.application.configuration.exception.BusinessException;
import org.jetbrains.annotations.NonNls;

public class MissingUsernameException extends BusinessException {
	public MissingUsernameException(@NonNls String message) {
		super(message, "Reporting Error", true);
	}
}
