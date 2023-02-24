package com.qbook.app.application.models.salesModels;

import lombok.Getter;

public class SalesEditedBookingModel {
	@Getter private String message;

	public SalesEditedBookingModel(String message) {
		this.message = message;
	}
}
