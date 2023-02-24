package com.qbook.app.application.models.scheduleModels;

import lombok.Data;

@Data
public class ScheduleTreatmentModel {
	private String treatmentId;
	private String treatmentName;
	private String treatmentDescription;
	private String treatmentPrice;
	private int treatmentDuration;
	private boolean special = false;
	private String specialPrice;
}
