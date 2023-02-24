package com.qbook.app.application.configuration.exception.scheduleExceptions;

import com.qbook.app.application.configuration.exception.BusinessException;
import org.jetbrains.annotations.NonNls;

public class MissingEmployeeTypeException extends BusinessException {
	public MissingEmployeeTypeException(@NonNls String message) {
		super(message, "Schedule Error", true);
	}
}
