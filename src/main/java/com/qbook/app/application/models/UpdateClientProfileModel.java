package com.qbook.app.application.models;

import lombok.Data;

@Data
public class UpdateClientProfileModel {
	private String firstName;
	private String lastName;
	private String emailAddress;
	private String mobileNumber;
	private String dateOfBirth;
}
