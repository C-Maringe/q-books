package com.qbook.app.application.models;

import lombok.Data;

@Data
public class NewBookingModel {
	private String employeeId;
	private String startDateTime;
	private NewBookingItemModel[] newBookingItemModel;
	private boolean depositRequired;
}
