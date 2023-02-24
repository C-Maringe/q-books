package com.qbook.app.application.models.configurationModels;

import com.qbook.app.domain.models.WorkingDay;
import lombok.Data;

import java.util.List;

@Data
public class NewApplicationConfigurationModel {
	private String id;
	private int sessionDurations;
	private int attendeesPerSession; // depecrate
	private int cancelNotice;
	private int bookingNotice;
	private String workStartTime;// depecrate
	private String workEndTime;// depecrate
	private boolean availableWeekends;
	private String[] daysAvailable;// depecrate
	private List<WorkingDay> workingDays;
	private double depositThreshold;
	private double depositPercentage;
}
