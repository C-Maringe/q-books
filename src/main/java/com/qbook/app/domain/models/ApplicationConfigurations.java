package com.qbook.app.domain.models;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(value = "app_configurations_collection")
public class ApplicationConfigurations {

	@Id
	private ObjectId id;
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
