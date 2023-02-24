package com.qbook.app.application.models.scheduleModels;

import lombok.Data;

@Data
public class ScheduleNewBlockoutTimeModel {
	private String blockoutTimeTitle;
	private String startDateTime;
	private String endDateTime;
	private String[] employees;
}
