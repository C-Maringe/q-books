package com.qbook.app.application.models.scheduleModels;

import lombok.Data;

@Data
public class ScheduleNewBlockoutTimeForWorkingDayModel {
	private String blockoutTimeTitle;
	private String startDateTime;
	private String endDateTime;
	private String employeesId;
	private String workingDayId;
}
