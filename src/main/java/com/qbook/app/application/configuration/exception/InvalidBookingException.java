package com.qbook.app.application.configuration.exception;

import org.jetbrains.annotations.NonNls;

public class InvalidBookingException extends BusinessException {

	public InvalidBookingException(@NonNls String message) {
		super(message, "Booking Error", true);
	}
}
