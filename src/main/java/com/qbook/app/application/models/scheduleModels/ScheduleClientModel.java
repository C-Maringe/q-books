package com.qbook.app.application.models.scheduleModels;


import lombok.Getter;

public class ScheduleClientModel {
	@Getter private String clientId;
	@Getter private String clientFullName;

	public ScheduleClientModel(String clientId, String clientFullName) {
		this.clientId = clientId;
		this.clientFullName = clientFullName;
	}
}
