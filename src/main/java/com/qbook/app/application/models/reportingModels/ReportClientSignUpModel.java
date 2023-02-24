package com.qbook.app.application.models.reportingModels;

import com.qbook.app.domain.models.ContactDetails;
import lombok.Data;

@Data
public class ReportClientSignUpModel {
	private String firstName;
	private String lastName;
	private boolean isActive;
	private Long dateRegistered;
	private ContactDetails contactDetails;
	private int totalBookingsMade;
}
