package com.qbook.app.application.models.employeeModels;

import lombok.Data;

@Data
public class ViewEmployeeModel {
	private String userId;
	private String firstName;
	private String lastName;
	private String emailAddress;
	private boolean active;
	private boolean bookConsultationFirst;
	private String employeeType;
	private String employeeLevel;
	private String contactDetails;
}
