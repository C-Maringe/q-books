package com.qbook.app.application.models.scheduleModels;

import lombok.Data;

@Data
public class ScheduleNewBookingModel {
	private String clientId;
	private String employeeId;
	private String startDateTime;
	private ScheduleNewBookingItemModel[] scheduleNewBookingItemModels;
	private boolean depositRequired;
}
