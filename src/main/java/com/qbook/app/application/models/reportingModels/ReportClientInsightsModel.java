package com.qbook.app.application.models.reportingModels;

import lombok.Data;

@Data
public class ReportClientInsightsModel {
	private String treatmentName;
	private int totalBooked;

	public ReportClientInsightsModel(String treatmentName, int totalBooked) {
		this.treatmentName = treatmentName;
		this.totalBooked = totalBooked;
	}
}
