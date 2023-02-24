package com.qbook.app.application.models.reportingModels;

import com.qbook.app.domain.models.ContactDetails;
import lombok.Data;

@Data
public class ReportClientSearchModel {
	private String id;
	private String firstName;
	private String lastName;
	private boolean isActive;
	private String role;
	private Long dateRegistered;
	private ContactDetails contactDetails;
}
