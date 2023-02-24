package com.qbook.app.application.models.configurationModels;

import com.qbook.app.domain.models.WorkingDay;
import lombok.Data;

import java.util.List;

@Data
public class ApplicationConfigurationModel {
	private String id;
	private int sessionDurations;
	private int attendeesPerSession;
	private int cancelNotice;
	private int bookingNotice;
	private String workStartTime;//deprecate
	private String workEndTime;//deprecate
	private boolean availableWeekends;
	private List<String> daysAvailable;//deprecate
	private List<WorkingDay> workingDays;
	private double depositThreshold;
	private double depositPercentage;
}
