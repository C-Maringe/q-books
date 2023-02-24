package com.qbook.app.application.configuration.exception.reportingExceptions;

import com.qbook.app.application.configuration.exception.BusinessException;
import org.jetbrains.annotations.NonNls;

public class MissingDateRangeException extends BusinessException {
	public MissingDateRangeException(@NonNls String message) {
		super(message, "Reporting Error", true);
	}
}
