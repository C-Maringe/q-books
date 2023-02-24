package com.qbook.app.application.models.reportingModels;

import lombok.Data;

@Data
public class ReportClientEmailInvoiceModel {
	private String clientEmail;
	private String from;
	private String to;
	private String employeeEmail;
}
