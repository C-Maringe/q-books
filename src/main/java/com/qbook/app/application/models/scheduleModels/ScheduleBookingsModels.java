package com.qbook.app.application.models.scheduleModels;

import lombok.Data;

@Data
public class ScheduleBookingsModels {
	private String bookingId;
	private String startTime;
	private String endTime;
	private String employeeFullName;
	private String clientFullName;
	private String title;
	private String description;
	private boolean canView;
	private boolean canCancel;
	private boolean depositPaid;
}
