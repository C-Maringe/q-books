package com.qbook.app.application.models.employeeModels;

import com.qbook.app.application.models.UserPermissions;
import com.qbook.app.domain.models.ContactDetails;
import lombok.Data;

@Data
public class NewEmployeeModel {
	private String firstName;
	private String lastName;
	private String password;
	private String clearPassword;
	private String employeeType;
	private String employeeLevel;
	private boolean mustBookConsultationFirstTime;
	private ContactDetails contactDetails;
	private UserPermissions userPermissions;
}
