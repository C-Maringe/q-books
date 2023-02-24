package com.qbook.app.application.models.reportingModels;

import lombok.Data;

@Data
public class ReportClientEmailModel {
	private String clientId;
	private String title;
	private String message;
}
