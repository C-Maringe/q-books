package com.qbook.app.application.models;

import lombok.Data;

@Data
public class NewJoinBookingCancellationQueueModel {
	private String startDate;
	private String endDate;
	private String startTime;
	private String endTime;
	private String employeeId;
}
