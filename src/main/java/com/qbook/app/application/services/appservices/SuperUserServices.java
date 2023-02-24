package com.qbook.app.application.services.appservices;

import com.qbook.app.application.models.employeeModels.NewEmployeeWorkingDayModel;
import com.qbook.app.application.models.employeeModels.*;
import com.qbook.app.domain.models.EmployeeWorkingDay;
import com.qbook.app.domain.models.UserPermission;

import java.util.List;

public interface SuperUserServices {

	EmployeeRegisteredModel registerNewEmployee(NewEmployeeModel newEmployeeModel);

	List<UserPermission> viewAllSystemPermissions();

	CreatedEmployeeWorkingDayModel createEmployeeWorkingDayModel(NewEmployeeWorkingDayModel newEmployeeWorkingDayModel);

	EmployeeWorkingDay addNewEmployeeWorkingDay(NewEmployeeWorkingDayModel newEmployeeWorkingDayModel);

	List<EmployeeWorkingDayModel> viewAllEmployeeWorkingDays(String employeeId, int month, int year);

	RemovedEmployeeWorkingDayModel removeEmployeeWorkingDay(String employeeId, String employeeWorkingDayId);

	EmployeeUpdatedModel disableAccount(String clientId);

	EmployeeUpdatedModel enableAccount(String clientId);

	List<ViewEmployeeType> getAllEmployeeTypes();

	List<ViewEmployeeModel> getAllEmployees();

    ViewFullEmployeeModel getEmployee(String employeeId);

	EmployeeUpdatedModel updateEmployee(UpdateEmployeeModel employee);

}
