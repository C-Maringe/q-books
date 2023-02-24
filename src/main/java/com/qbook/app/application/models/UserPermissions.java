package com.qbook.app.application.models;

import com.qbook.app.application.models.userPermissionsModels.*;
import lombok.Data;

@Data
public class UserPermissions {
	private Analytics analytics;
	private Bookings bookings;
	private Clients clients;
	private Configurations configurations;
	private Employees employees;
	private Marketing marketing;
	private Reporting reporting;
	private Sales sales;
	private Schedule schedule;
	private Treatments treatments;
	private Products products;
}
