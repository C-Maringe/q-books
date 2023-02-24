package com.qbook.app.application.models.employeeModels;

import com.qbook.app.domain.models.ContactDetails;
import com.qbook.app.domain.models.UserPermission;
import lombok.Data;

import java.util.List;

@Data
public class ViewFullEmployeeModel {
	private String userId;
	private String firstName;
	private String lastName;
	private String employeeType;
	private String employeeLevel;
	private boolean mustBookConsultationFirstTime;
	private boolean active;
	private ContactDetails contactDetails;
	private List<UserPermission> userPermissionList;
}
